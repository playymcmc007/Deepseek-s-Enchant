package com.playymcmc007.DeepSeeksEnchant.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GiantTreeFeature extends Feature<NoneFeatureConfiguration> {
    public static final Map<Block, BlockState> LOG_MAP = Map.of(
            Blocks.OAK_SAPLING, Blocks.OAK_LOG.defaultBlockState(),
            Blocks.BIRCH_SAPLING, Blocks.BIRCH_LOG.defaultBlockState(),
            Blocks.SPRUCE_SAPLING, Blocks.SPRUCE_LOG.defaultBlockState(),
            Blocks.JUNGLE_SAPLING, Blocks.JUNGLE_LOG.defaultBlockState(),
            Blocks.ACACIA_SAPLING, Blocks.ACACIA_LOG.defaultBlockState(),
            Blocks.DARK_OAK_SAPLING, Blocks.DARK_OAK_LOG.defaultBlockState(),
            Blocks.MANGROVE_PROPAGULE, Blocks.MANGROVE_LOG.defaultBlockState(),
            Blocks.CHERRY_SAPLING, Blocks.CHERRY_LOG.defaultBlockState()
    );

    public static final Map<Block, BlockState> LEAVES_MAP = Map.of(
            Blocks.OAK_SAPLING, Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.BIRCH_SAPLING, Blocks.BIRCH_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.SPRUCE_SAPLING, Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.JUNGLE_SAPLING, Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.ACACIA_SAPLING, Blocks.ACACIA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.DARK_OAK_SAPLING, Blocks.DARK_OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.MANGROVE_PROPAGULE, Blocks.MANGROVE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true),
            Blocks.CHERRY_SAPLING, Blocks.CHERRY_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true)
    );
    private final Set<BlockPos> generatedLogs = new HashSet<>();
    // 终极配置
    private static final int TRUNK_RADIUS = 40;       // 树干半径
    private static final int BRANCH_START = 50;       // 分叉初始高度
    private static final int MIN_HEIGHT = 100;         // 最小高度
    private static final int HEIGHT_VARIATION = 50;  // 高度变化
    private static final int CANOPY_RADIUS = 500;    // 树冠半径
    private static final int CANOPY_HEIGHT = 50;     // 树冠厚度
    private BlockPos basePos;
    private int treeActualHeight;
    private final Map<Integer, Integer> trunkRadiusCache = new HashMap<>();
    public GiantTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        generatedLogs.clear();
        WorldGenLevel level = context.level();
        BlockPos basePos = context.origin();
        RandomSource rand = context.random();

        int height = MIN_HEIGHT + rand.nextInt(HEIGHT_VARIATION);

        Block sapling = level.getBlockState(basePos).getBlock();
        if (!LOG_MAP.containsKey(sapling)) {
            return false;
        }
        BlockState log = LOG_MAP.get(sapling);
        BlockState leaves = LEAVES_MAP.get(sapling);

        generateWithMaterials(
                context.level(),
                context.origin(),
                log,
                leaves,
                context.random()
        );
        checkAndAddLeafCoverage(context.level());
        return true;
    }

    public void generateWithMaterials(WorldGenLevel level, BlockPos pos,
                                      BlockState log, BlockState leaves,
                                      RandomSource random) {
        this.basePos = pos;
        this.treeActualHeight = MIN_HEIGHT + random.nextInt(HEIGHT_VARIATION);

        generateColossalTrunk(level, pos, treeActualHeight, log, leaves, random);
        generateUltimateCanopy(level, pos.above(treeActualHeight), leaves, random);
    }

    // ========== 主干生成 ==========
    private void generateColossalTrunk(WorldGenLevel level, BlockPos base, int height,
                                       BlockState log, BlockState leaves, RandomSource rand) {
        for (int y = 0; y < height; y++) {
            int radius = calculateTrunkRadius(y);

            // 实心树干生成
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + z*z <= radius*radius) {
                        safeSetBlock(level, base.offset(x, y, z), log);
                    }
                }
            }

            // 超早分叉系统
            if (y >= BRANCH_START && y % 2 == 0) {
                generatePrimaryBranches(level, base.above(y), radius + 1, log, leaves, rand);
            }
        }
    }

    // ========== 主分支生成 ==========
    private void generatePrimaryBranches(WorldGenLevel level, BlockPos startPos, int startRadius,
                                         BlockState log, BlockState leaves, RandomSource rand) {
        // 主分支
        int branchCount = 40 + rand.nextInt(40);

        for (int i = 0; i < branchCount; i++) {
            double angle = 2 * Math.PI * (i + rand.nextFloat() * 0.7f) / branchCount;
            double xDir = Math.cos(angle);
            double zDir = Math.sin(angle);

            // 超长树枝
            int length = 40 + rand.nextInt(40);
            float upwardSlope = 0.1f + rand.nextFloat() * 0.3f;

            BlockPos current = startPos;
            for (int l = 0; l < length; l++) {
                // 动态弯曲
                xDir += (rand.nextFloat() - 0.5f) * 0.2f;
                zDir += (rand.nextFloat() - 0.5f) * 0.2f;
                upwardSlope = Math.max(0, upwardSlope + (rand.nextFloat() - 0.5f) * 0.15f);

                current = current.offset(
                        (int)Math.round(xDir),
                        (int)Math.round(upwardSlope),
                        (int)Math.round(zDir)
                );

                safeSetBlock(level, current, log);

                // 每4格生成次级分支（更多分叉点）
                if (l > 3 && l % 4 == 0) {
                    generateSecondaryBranches(level, current, log, leaves, rand, 4 + rand.nextInt(4));
                }

                // 每个节点生成超密树叶
                generateLeafSphere(level, current, log, leaves, rand, 3 + rand.nextInt(3));
            }
        }
    }

    // ========== 次级分支生成（新增完整实现） ==========
    private void generateSecondaryBranches(WorldGenLevel level, BlockPos start,
                                           BlockState log, BlockState leaves,
                                           RandomSource rand, int count) {
        for (int i = 0; i < count; i++) {
            // 随机3D方向（偏向水平）
            float horizontalAngle = rand.nextFloat() * (float)Math.PI * 2;
            float verticalAngle = rand.nextFloat() * 0.4f - 0.2f;

            // 长次级分支=
            int length = 20 + rand.nextInt(20);
            BlockPos current = start;

            for (int l = 0; l < length; l++) {
                current = current.offset(
                        (int)Math.round(Math.cos(horizontalAngle) * (1 + rand.nextFloat() * 0.2)),
                        (int)Math.round(verticalAngle),
                        (int)Math.round(Math.sin(horizontalAngle) * (1 + rand.nextFloat() * 0.2))
                );

                safeSetBlock(level, current, log);
                generateLeafSphere(level, current, log, leaves, rand, 2 + rand.nextInt(2));

                // 60%几率生成三级分支
                if (l > 2 && rand.nextFloat() > 0.4f) {
                    generateTertiaryBranches(level, current, log, leaves, rand);
                }
            }
        }
    }

    // ========== 三级分支生成 ==========
    private void generateTertiaryBranches(WorldGenLevel level, BlockPos start,
                                          BlockState log, BlockState leaves,
                                          RandomSource rand) {
        int count = 10 + rand.nextInt(10);
        for (int i = 0; i < count; i++) {
            int length = 3 + rand.nextInt(4);
            BlockPos current = start;

            for (int l = 0; l < length; l++) {
                current = current.relative(Direction.getRandom(rand))
                        .above(rand.nextInt(2) - rand.nextInt(1));
                safeSetBlock(level, current, log);
                generateLeafSphere(level, current, log, leaves, rand, 1 + rand.nextInt(2));
            }
        }
    }

    // ========== 终极树冠生成 ==========
    private void generateUltimateCanopy(WorldGenLevel level, BlockPos canopyTop,
                                        BlockState leaves, RandomSource rand) {
        // 1. 顶部小圆帽 (15%高度)
        generateCanopyTier(level, canopyTop,
                (int)(CANOPY_RADIUS * 0.25),  // 最小半径
                (int)(CANOPY_HEIGHT * 0.15),
                0.1f, leaves, rand);

        // 2. 上过渡层 (20%高度)
        generateCanopyTier(level, canopyTop.below((int)(CANOPY_HEIGHT * 0.15)),
                (int)(CANOPY_RADIUS * 0.55),
                (int)(CANOPY_HEIGHT * 0.2),
                0.2f, leaves, rand);

        // 3. 主冠层 (30%高度)
        generateCanopyTier(level, canopyTop.below((int)(CANOPY_HEIGHT * 0.35)),
                (int)(CANOPY_RADIUS * 0.85),  // 最大半径
                (int)(CANOPY_HEIGHT * 0.3),
                0.05f, leaves, rand);  // 几乎不下垂

        // 4. 下过渡层 (20%高度)
        generateCanopyTier(level, canopyTop.below((int)(CANOPY_HEIGHT * 0.65)),
                (int)(CANOPY_RADIUS * 0.65),
                (int)(CANOPY_HEIGHT * 0.2),
                0.3f, leaves, rand);

        // 5. 底部边缘层 (15%高度)
        generateCanopyTier(level, canopyTop.below((int)(CANOPY_HEIGHT * 0.85)),
                (int)(CANOPY_RADIUS * 0.4),
                (int)(CANOPY_HEIGHT * 0.15),
                0.6f, leaves, rand);  // 强烈下垂
    }
    // ========== 实心树冠层生成 ==========
    private void generateCanopyTier(WorldGenLevel level, BlockPos center, int maxRadius,
                                    int height, float droopFactor, BlockState leaves, RandomSource rand) {
        for (int layer = 0; layer < height; layer++) {
            float progress = (float)layer / height;
            int radius = (int)(maxRadius * (1 - progress * droopFactor));

            int currentTrunkRadius = getCachedTrunkRadius(center.getY() - layer);

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + z*z);
                    BlockPos pos = center.offset(x, -layer, z);
                    if (distance <= radius && distance > currentTrunkRadius) {
                        safeSetBlock(level, pos, leaves);

                        // 边缘下垂效果
                        if (distance > radius * 0.7) {
                            generateDanglingLeaves(level, pos, leaves, rand, 2 + rand.nextInt(4));
                        }
                    }
                }
            }
        }
    }


    private void generateLeafSphere(WorldGenLevel level, BlockPos center,
                                    BlockState log,BlockState leaves, RandomSource rand, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance <= radius && !level.getBlockState(pos).is(log.getBlock())) {
                        safeSetBlock(level, pos, leaves);
                    }
                }
            }
        }
    }

    private void generateDanglingLeaves(WorldGenLevel level, BlockPos start,
                                        BlockState leaves, RandomSource rand, int length) {
        BlockPos current = start;
        for (int i = 0; i < length; i++) {
            current = current.below();
            if (level.getBlockState(current).isAir()) {
                safeSetBlock(level, current, leaves);
            } else {
                break;
            }
        }
    }

    private int calculateTrunkRadius(int currentY) {
        int relativeY = currentY - basePos.getY();

        if (relativeY <= treeActualHeight * 0.8f) {
            return TRUNK_RADIUS;
        }
        int growthSteps = (relativeY - (int)(treeActualHeight * 0.8f)) / 2;
        return TRUNK_RADIUS + growthSteps;
    }

    private int getCachedTrunkRadius(int y) {
        return trunkRadiusCache.computeIfAbsent(y,
                k -> calculateTrunkRadius(k));
    }

    private void checkAndAddLeafCoverage(WorldGenLevel level) {
        Set<BlockPos> logsNeedingCover = new HashSet<>();

        // 检查每个原木方块的6个面是否有树叶
        for (BlockPos logPos : generatedLogs) {
            boolean covered = false;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = logPos.relative(dir);
                BlockState neighborState = level.getBlockState(neighbor);
                if (neighborState.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock) {
                    covered = true;
                    break;
                }
            }
            if (!covered) {
                logsNeedingCover.add(logPos);
            }
        }

        // 为需要覆盖的原木添加树叶
        for (BlockPos logPos : logsNeedingCover) {
            for (Direction dir : Direction.values()) {
                BlockPos coverPos = logPos.relative(dir);
                if (level.getBlockState(coverPos).isAir()) {
                    // 使用与树类型匹配的树叶
                    BlockState leaves = findMatchingLeaves(level.getBlockState(logPos));
                    if (leaves != null) {
                        level.setBlock(coverPos, leaves, 3);
                    }
                }
            }
        }
    }

    private BlockState findMatchingLeaves(BlockState logState) {
        for (Map.Entry<Block, BlockState> entry : LOG_MAP.entrySet()) {
            if (entry.getValue().getBlock() == logState.getBlock()) {
                return LEAVES_MAP.get(entry.getKey());
            }
        }
        return null;
    }

    private void safeSetBlock(WorldGenLevel level, BlockPos pos, BlockState newState) {
        if (level.isOutsideBuildHeight(pos)) {
            return;
        }

        BlockState currentState = level.getBlockState(pos);
        if (currentState.isAir() || currentState.canBeReplaced()) {
            level.setBlock(pos, newState, 3);
            if (newState.getBlock() instanceof net.minecraft.world.level.block.RotatedPillarBlock) {
                generatedLogs.add(pos);
            }
        }
    }
}