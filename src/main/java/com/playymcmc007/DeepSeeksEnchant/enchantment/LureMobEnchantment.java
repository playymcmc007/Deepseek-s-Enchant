package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LureMobEnchantment extends Enchantment {

    public static final Map<Integer, Integer> ENCHANTED_BOATS = new ConcurrentHashMap<>();

    private static final Map<UUID, ItemStack> RECENT_BOAT_ITEMS = new ConcurrentHashMap<>();

    private static final int MAX_BOAT_PASSENGERS = 2;

    public LureMobEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.BoatItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    public static void addEnchantedBoat(Boat boat, int level) {
        if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
            return;
        }
        if (boat != null && boat.isAlive()) {
            ENCHANTED_BOATS.put(boat.getId(), level);
            CompoundTag nbt = boat.getPersistentData();
            nbt.putInt("LureMobLevel", level);
            nbt.putInt("LureMobEntityId", boat.getId());
        }
    }

    public static void removeEnchantedBoat(int boatId) {
        ENCHANTED_BOATS.remove(boatId);
    }

    private static Optional<Boat> getValidBoat(Level level, int boatId) {
        if (level == null) return Optional.empty();

        Entity entity = level.getEntity(boatId);

        if (entity instanceof Boat boat) {
            if (boat.isAlive() && !boat.isRemoved() && boat.isAddedToWorld()) {
                return Optional.of(boat);
            }
        }
        return Optional.empty();
    }

    private static boolean hasEmptySeat(Boat boat) {
        return boat.getPassengers().size() < MAX_BOAT_PASSENGERS;
    }

    private static int getEmptySeatCount(Boat boat) {
        return MAX_BOAT_PASSENGERS - boat.getPassengers().size();
    }

    private static boolean checkItemForEnchantment(ItemStack stack) {
        if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
            return false;
        }
        return stack.getItem() instanceof net.minecraft.world.item.BoatItem &&
                stack.getEnchantmentLevel(ModEnchantments.LURE_MOB.get()) > 0;
    }

    @Mod.EventBusSubscriber(modid = "deepseeksenchant", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class LureMobEnchantmentEventHandler {

        private static long lastCleanupTime = 0;

        @SubscribeEvent
        public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
            if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
                return;
            }
            if (event.getLevel().isClientSide()) return;

            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof net.minecraft.world.item.BoatItem) {
                RECENT_BOAT_ITEMS.put(event.getEntity().getUUID(), stack.copy());
            }
        }

        @SubscribeEvent
        public static void onBoatPlaced(EntityJoinLevelEvent event) {
            if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
                return;
            }
            if (event.getLevel().isClientSide()) return;

            if (event.getEntity() instanceof Boat boat) {

                CompoundTag nbt = boat.getPersistentData();
                if (nbt.contains("LureMobLevel") && nbt.contains("LureMobEntityId")) {
                    int savedLevel = nbt.getInt("LureMobLevel");
                    int savedEntityId = nbt.getInt("LureMobEntityId");
                    if (savedLevel > 0 && boat.getId() == savedEntityId) {
                        addEnchantedBoat(boat, savedLevel);
                        return;
                    }
                }

                AABB searchArea = new AABB(boat.blockPosition()).inflate(10);
                List<Player> nearbyPlayers = event.getLevel().getEntitiesOfClass(Player.class, searchArea);

                boolean foundEnchantment = false;

                for (Player player : nearbyPlayers) {
                    ItemStack recentBoatItem = RECENT_BOAT_ITEMS.get(player.getUUID());
                    if (recentBoatItem != null && recentBoatItem.getItem() instanceof net.minecraft.world.item.BoatItem) {
                        int enchantLevel = recentBoatItem.getEnchantmentLevel(ModEnchantments.LURE_MOB.get());
                        if (enchantLevel > 0) {
                            addEnchantedBoat(boat, enchantLevel);
                            RECENT_BOAT_ITEMS.remove(player.getUUID());
                            foundEnchantment = true;
                            break;
                        }
                    }

                    ItemStack mainHand = player.getMainHandItem();
                    ItemStack offHand = player.getOffhandItem();

                    if (checkItemForEnchantment(mainHand)) {
                        int enchantLevel = mainHand.getEnchantmentLevel(ModEnchantments.LURE_MOB.get());
                        if (enchantLevel > 0) {
                            addEnchantedBoat(boat, enchantLevel);
                            foundEnchantment = true;
                            break;
                        }
                    }

                    if (checkItemForEnchantment(offHand)) {
                        int enchantLevel = offHand.getEnchantmentLevel(ModEnchantments.LURE_MOB.get());
                        if (enchantLevel > 0) {
                            addEnchantedBoat(boat, enchantLevel);
                            foundEnchantment = true;
                            break;
                        }
                    }
                }
            }
        }
        @SubscribeEvent
        public static void onEntityRemoved(EntityLeaveLevelEvent event) {
            if (event.getEntity() instanceof Boat boat) {
                removeEnchantedBoat(boat.getId());
            }
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.LevelTickEvent event) {
            if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
                // 如果附魔被禁用，清空所有数据
                if (!ENCHANTED_BOATS.isEmpty()) {
                    ENCHANTED_BOATS.clear();
                }
                return;
            }
            if (event.phase != TickEvent.Phase.END) return;

            Level level = event.level;
            if (level.isClientSide()) return;

            if (level.getGameTime() % 60 != 0) return;

            Map<Integer, Integer> boatsToProcess = new HashMap<>(ENCHANTED_BOATS);

            for (Map.Entry<Integer, Integer> entry : boatsToProcess.entrySet()) {
                int boatId = entry.getKey();
                int enchantLevel = entry.getValue();

                Optional<Boat> boatOpt = getValidBoat(level, boatId);
                if (boatOpt.isEmpty()) {
                    removeEnchantedBoat(boatId);
                    continue;
                }

                Boat boat = boatOpt.get();

                int emptySeats = getEmptySeatCount(boat);
                if (emptySeats == 0) {
                    continue;
                }

                double range = 8 + enchantLevel * 3;
                AABB area = new AABB(boat.blockPosition()).inflate(range);

                List<LivingEntity> nearbyMobs = level.getEntitiesOfClass(LivingEntity.class, area)
                        .stream()
                        .filter(e -> !(e instanceof Player) && e.isAlive() && !e.isPassenger())
                        .collect(Collectors.toList());

                if (nearbyMobs.isEmpty()) continue;

                int mobsToAttract = Math.min(emptySeats, nearbyMobs.size());

                Collections.shuffle(nearbyMobs);
                List<LivingEntity> targets = nearbyMobs.subList(0, mobsToAttract);

                for (LivingEntity target : targets) {

                    Vec3 boatPos = boat.position();
                    target.teleportTo(boatPos.x, boatPos.y + 0.5, boatPos.z);

                    target.startRiding(boat, true);

                    emptySeats--;
                    if (emptySeats == 0) {
                        break;
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCleanupTime > 30000) {
                int before = RECENT_BOAT_ITEMS.size();
                RECENT_BOAT_ITEMS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
                int after = RECENT_BOAT_ITEMS.size();
                lastCleanupTime = currentTime;
            }
        }

        @SubscribeEvent
        public static void onServerStarting(net.minecraftforge.event.server.ServerStartingEvent event) {
            ENCHANTED_BOATS.clear();
            RECENT_BOAT_ITEMS.clear();
        }
        @SubscribeEvent
        public static void onConfigReload(ModConfigEvent.Reloading event) {
            if (event.getConfig().getModId().equals("deepseeksenchant")) {
                if (!EnchantmentToggleConfig.LURE_MOD_ENABLED.get()) {
                    ENCHANTED_BOATS.clear();
                    RECENT_BOAT_ITEMS.clear();
                }
            }
        }
    }
}