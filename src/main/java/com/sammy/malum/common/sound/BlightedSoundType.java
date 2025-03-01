package com.sammy.malum.common.sound;

import com.sammy.malum.registry.common.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.lodestar.lodestone.systems.sound.ExtendedSoundType;

import java.util.function.Supplier;

public class BlightedSoundType extends ExtendedSoundType {
    public BlightedSoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSoundIn, Supplier<SoundEvent> stepSoundIn, Supplier<SoundEvent> placeSoundIn, Supplier<SoundEvent> hitSoundIn, Supplier<SoundEvent> fallSoundIn) {
        super(volumeIn, pitchIn, breakSoundIn, stepSoundIn, placeSoundIn, hitSoundIn, fallSoundIn);
    }

    @Override
    public void onPlayBreakSound(Level level, BlockPos pos) {
        level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundRegistry.MINOR_BLIGHT_MOTIF.get(), SoundSource.BLOCKS, (getVolume() + 1.0F) / 4.0F, getPitch() * 1.9F, false);
    }

    @Override
    public void onPlayStepSound(Level level, BlockPos pos, BlockState state, SoundSource category) {
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.MINOR_BLIGHT_MOTIF.get(), category, getVolume() * 0.15F, getPitch()* 1.6F);
    }

    @Override
    public void onPlayPlaceSound(Level level, BlockPos pos, Player player) {
        level.playSound(player, pos, SoundRegistry.MINOR_BLIGHT_MOTIF.get(), SoundSource.BLOCKS, (getVolume() + 1.0F) / 2.0F, getPitch() * 1.8F);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void onPlayHitSound(BlockPos pos) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundRegistry.MINOR_BLIGHT_MOTIF.get(), SoundSource.BLOCKS, (getVolume() + 1.0F) / 8.0F, getPitch() * 2F, pos));
    }

    @Override
    public void onPlayFallSound(Level level, BlockPos pos, SoundSource category) {
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.MAJOR_BLIGHT_MOTIF.get(), category, getVolume() * 0.5F, getPitch() * 1.7F);
    }
}