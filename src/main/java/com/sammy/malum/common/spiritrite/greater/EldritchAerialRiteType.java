package com.sammy.malum.common.spiritrite.greater;

import com.sammy.malum.client.vfx.*;
import com.sammy.malum.common.block.curiosities.totem.*;
import com.sammy.malum.common.packets.particle.curiosities.rite.*;
import com.sammy.malum.core.systems.rites.*;
import com.sammy.malum.registry.common.*;
import com.sammy.malum.registry.common.block.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.stats.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraftforge.network.*;

import java.util.*;

import static com.sammy.malum.registry.common.PacketRegistry.*;
import static com.sammy.malum.registry.common.SpiritTypeRegistry.*;

public class EldritchAerialRiteType extends MalumRiteType {
    public EldritchAerialRiteType() {
        super("greater_aerial_rite", ELDRITCH_SPIRIT, ARCANE_SPIRIT, AERIAL_SPIRIT, AERIAL_SPIRIT);
    }

    @Override
    public MalumRiteEffect getNaturalRiteEffect() {
        return new BlockAffectingRiteEffect() {
            @Override
            public void riteEffect(TotemBaseBlockEntity totemBase) {
                Level level = totemBase.getLevel();
                if (level instanceof ServerLevel serverLevel) {
                    BlockPos pos = totemBase.getBlockPos();
                    getBlocksUnderBase(totemBase, Block.class).forEach(p -> {
                        BlockState stateBelow = level.getBlockState(p.below());
                        if (FallingBlock.isFree(stateBelow) || !stateBelow.canOcclude() || stateBelow.is(BlockTags.SLABS)) {
                            BlockState state = level.getBlockState(p);
                            if (!state.isAir() && level.getBlockEntity(p) == null && canSilkTouch(serverLevel, pos, state)) {
                                FallingBlockEntity.fall(level, p, state);
                                level.playSound(null, p, SoundRegistry.AERIAL_FALL.get(), SoundSource.BLOCKS, 0.5f, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                                MALUM_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new AerialBlockFallRiteEffectPacket(AERIAL_SPIRIT.getPrimaryColor(), p));
                            }
                        }
                    });
                }
            }
        };
    }

    @Override
    public MalumRiteEffect getCorruptedEffect() {
        return new EntityAffectingRiteEffect() {
            @Override
            public void riteEffect(TotemBaseBlockEntity totemBase) {
                getNearbyEntities(totemBase, ServerPlayer.class).forEach(p -> {
                    ServerStatsCounter stats = p.getStats();
                    Stat<ResourceLocation> sleepStat = Stats.CUSTOM.get(Stats.TIME_SINCE_REST);
                    int value = stats.getValue(sleepStat);
                    stats.setValue(p, sleepStat, Math.max(0, value-500));
                    ParticleEffectTypeRegistry.HEXING_SMOKE.createEntityEffect(p, new ColorEffectData(AERIAL_SPIRIT));
                });
            }
        };
    }

    private static final List<Item> TOOLS = List.of(Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE);
    // From Botania, modified slightly

    private static ItemStack getToolForState(BlockState state) {
        if (!state.requiresCorrectToolForDrops()) {
            return new ItemStack(Items.NETHERITE_PICKAXE);
        } else {
            for (Item item : TOOLS) {
                ItemStack stack = new ItemStack(item);
                if (stack.isCorrectToolForDrops(state)) {
                    return stack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    private static boolean canSilkTouch(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.is(BlockTagRegistry.GREATER_AERIAL_WHITELIST)) {
            return true;
        }

        ItemStack harvestToolStack = getToolForState(state);
        if (harvestToolStack.isEmpty()) {
            return false;
        }
        harvestToolStack.enchant(Enchantments.SILK_TOUCH, 1);
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, harvestToolStack);
        Item blockItem = state.getBlock().asItem();
        return drops.stream().anyMatch(s -> s.getItem() == blockItem);
    }
}
