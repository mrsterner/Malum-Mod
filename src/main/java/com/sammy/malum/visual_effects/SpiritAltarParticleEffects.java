package com.sammy.malum.visual_effects;

import com.sammy.malum.common.block.curiosities.obelisk.RunewoodObeliskBlockEntity;
import com.sammy.malum.common.block.curiosities.spirit_altar.IAltarAccelerator;
import com.sammy.malum.common.block.curiosities.spirit_altar.SpiritAltarBlockEntity;
import com.sammy.malum.common.block.storage.MalumItemHolderBlockEntity;
import com.sammy.malum.common.item.spirit.SpiritShardItem;
import com.sammy.malum.common.recipe.SpiritInfusionRecipe;
import com.sammy.malum.core.systems.spirit.MalumSpiritType;
import com.sammy.malum.visual_effects.networked.data.ColorEffectData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.helpers.DataHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.LodestoneWorldParticleActor;
import team.lodestar.lodestone.systems.particle.builder.SparkParticleBuilder;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;

import java.util.function.Consumer;

import static com.sammy.malum.visual_effects.SpiritLightSpecs.spiritLightSpecs;

public class SpiritAltarParticleEffects {

    public static MalumSpiritType getCentralSpiritType(SpiritAltarBlockEntity altar) {
        final LodestoneBlockEntityInventory spiritInventory = altar.spiritInventory;
        int spiritCount = spiritInventory.nonEmptyItemAmount;
        Item currentItem = spiritInventory.getStackInSlot(0).getItem();
        if (spiritCount > 1) {
            float duration = 30f * spiritCount;
            float gameTime = (altar.getLevel().getGameTime() % duration) / 30f;
            currentItem = spiritInventory.getStackInSlot(Mth.floor(gameTime)).getItem();
        }
        if (!(currentItem instanceof SpiritShardItem spiritItem)) {
            return null;
        }
        return spiritItem.type;
    }

    public static void passiveSpiritAltarParticles(SpiritAltarBlockEntity altar) {
        MalumSpiritType activeSpiritType = getCentralSpiritType(altar);
        if (activeSpiritType == null) {
            return;
        }
        Level level = altar.getLevel();
        var random = level.random;
        Vec3 itemPos = altar.getItemPos();
        LodestoneBlockEntityInventory spiritInventory = altar.spiritInventory;
        SpiritInfusionRecipe recipe = altar.recipe;
        if (recipe != null) {
            for (IAltarAccelerator accelerator : altar.accelerators) {
                if (accelerator != null) {
                    accelerator.addParticles(altar, activeSpiritType);
                }
            }
            SpiritLightSpecs.rotatingLightSpecs(level, itemPos, activeSpiritType, 0.5f, 3, b -> b.multiplyLifetime(1.2f).modifyData(b::getScaleData, d -> d.multiplyValue(1.2f)));
        }

        int spiritsRendered = 0;
        for (int i = 0; i < spiritInventory.slotCount; i++) {
            ItemStack item = spiritInventory.getStackInSlot(i);
            if (item.getItem() instanceof SpiritShardItem spiritSplinterItem) {
                Vec3 offset = altar.getSpiritItemOffset(spiritsRendered++, 0);
                activeSpiritType = spiritSplinterItem.type;
                BlockPos blockPos = altar.getBlockPos();
                Vec3 spiritPosition = new Vec3(blockPos.getX() + offset.x, blockPos.getY() + offset.y, blockPos.getZ() + offset.z);
                spiritLightSpecs(level, spiritPosition, activeSpiritType).spawnParticles();
                if (recipe != null) {
                    Vec3 velocity = itemPos.subtract(spiritPosition).normalize().scale(RandomHelper.randomBetween(random, 0.03f, 0.06f));
                    if (random.nextFloat() < 0.85f) {
                        var sparkParticles = SparkParticleEffects.spiritMotionSparks(level, spiritPosition, activeSpiritType);
                        sparkParticles.getBuilder().setMotion(velocity).modifyData(SparkParticleBuilder::getScaleData, d -> d.multiplyValue(1.2f));
                        sparkParticles.getBloomBuilder().setMotion(velocity);
                        sparkParticles.spawnParticles();
                    }
                    if (random.nextFloat() < 0.85f) {
                        var lightSpecs = SpiritLightSpecs.spiritLightSpecs(level, spiritPosition, activeSpiritType);
                        lightSpecs.getBuilder().multiplyLifetime(0.8f).setMotion(velocity.scale(1.5f)).modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(1.6f));
                        lightSpecs.spawnParticles();
                    }
                }
            }
        }
    }

    public static void eatItemParticles(SpiritAltarBlockEntity altar, MalumItemHolderBlockEntity holder, ColorEffectData colorData, ItemStack stack) {
        MalumSpiritType activeSpiritType = getCentralSpiritType(altar);
        if (activeSpiritType == null) {
            return;
        }
        Level level = altar.getLevel();
        long gameTime = level.getGameTime();
        var random = level.random;
        Vec3 altarTargetPos = altar.getItemPos();
        Vec3 holderTargetPos = holder.getItemPos();
        for (int i = 0; i < 2; i++) {
            SpiritLightSpecs.coolLookingShinyThing(level, holderTargetPos, activeSpiritType);
        }
        for (int i = 0; i < 16; i++) {
            MalumSpiritType cyclingSpiritType = colorData.getCyclingColorRecord().spiritType();
            Vec3 velocity = altarTargetPos.subtract(holderTargetPos).normalize().scale(RandomHelper.randomBetween(random, 0.01f, 0.02f));
            int finalI = i;
            Vec3 offsetPosition = DataHelper.rotatingRadialOffset(holderTargetPos, 0.5f, i, 16, gameTime, 160);
            final Consumer<LodestoneWorldParticleActor> behavior = p -> {
                if (level.getGameTime() > gameTime + finalI * 2 && level.getGameTime() < gameTime + (finalI + 4) * 2) {
                    p.setParticleMotion(p.getParticleSpeed().add(velocity));
                }
            };
            var lightSpecs = spiritLightSpecs(level, offsetPosition, cyclingSpiritType);
            lightSpecs.getBuilder().act(b -> b
                    .addTickActor(behavior)
                    .multiplyLifetime(2.5f)
                    .modifyData(b::getScaleData, d -> d.multiplyValue(RandomHelper.randomBetween(random, 1f, 2f))));
            lightSpecs.getBloomBuilder().act(b -> b
                    .addTickActor(behavior)
                    .multiplyLifetime(2f)
                    .modifyData(b::getScaleData, d -> d.multiplyValue(RandomHelper.randomBetween(random, 0.6f, 1.5f))));
            lightSpecs.spawnParticles();

            var crumbles = ItemCrumbleParticleEffects.spawnItemCrumbs(level, holderTargetPos, stack);
            crumbles.getBuilder().multiplyLifetime(1 + i / 16f).addTickActor(behavior);
            crumbles.spawnParticles();
            crumbles.getBuilder().setRandomOffset(0.2f);
            crumbles.spawnParticles();
        }
        Vec3 velocity = altarTargetPos.subtract(holderTargetPos).normalize().scale(RandomHelper.randomBetween(random, 0.01f, 0.02f));
        for (int i = 0; i < 16; i++) {
            int finalI = i;
            final Consumer<LodestoneWorldParticleActor> behavior = p -> {
                if (level.getGameTime() > gameTime + finalI && level.getGameTime() < gameTime + (finalI + 6)) {
                    p.setParticleMotion(p.getParticleSpeed().add(velocity));
                }
            };
            var crumbles = ItemCrumbleParticleEffects.spawnItemCrumbs(level, holderTargetPos, stack);
            crumbles.getBuilder().addTickActor(behavior);
            if (i % 2 == 0) {
                crumbles.spawnParticles();
            }
            crumbles.getBuilder().setRandomOffset(0.15f);
            crumbles.spawnParticles();
        }
    }

    public static void craftItemParticles(SpiritAltarBlockEntity altar, ColorEffectData colorData) {
        MalumSpiritType activeSpiritType = getCentralSpiritType(altar);
        if (activeSpiritType == null) {
            return;
        }
        Level level = altar.getLevel();
        long gameTime = level.getGameTime();
        var random = level.random;
        BlockPos altarPos = altar.getBlockPos();
        Vec3 targetPos = altar.getCentralItemOffset().add(altarPos.getX(), altarPos.getY(), altarPos.getZ());

        for (int i = 0; i < 2; i++) {
            MalumSpiritType cyclingSpiritType = colorData.getCyclingColorRecord().spiritType();
            SpiritLightSpecs.coolLookingShinyThing(level, targetPos, cyclingSpiritType);
        }
        for (int i = 0; i < 24; i++) {
            MalumSpiritType cyclingSpiritType = colorData.getCyclingColorRecord().spiritType();
            float xVelocity = RandomHelper.randomBetween(random, Easing.CUBIC_OUT, -0.075f, 0.075f);
            float yVelocity = RandomHelper.randomBetween(random, 0.2f, 0.5f);
            float zVelocity = RandomHelper.randomBetween(random, Easing.CUBIC_OUT, -0.075f, 0.075f);
            float gravityStrength = RandomHelper.randomBetween(random, 0.75f, 1f);
            if (random.nextFloat() < 0.85f) {
                var sparkParticles = SparkParticleEffects.spiritMotionSparks(level, targetPos, cyclingSpiritType);
                sparkParticles.getBuilder()
                        .disableNoClip()
                        .multiplyLifetime(2)
                        .setGravityStrength(gravityStrength)
                        .setMotion(xVelocity, yVelocity, zVelocity)
                        .modifyData(SparkParticleBuilder::getScaleData, d -> d.multiplyValue(2f));
                sparkParticles.getBloomBuilder()
                        .disableNoClip()
                        .multiplyLifetime(2)
                        .setGravityStrength(gravityStrength)
                        .setMotion(xVelocity, yVelocity, zVelocity)
                        .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(1.25f));
                sparkParticles.spawnParticles();
            }
            if (random.nextFloat() < 0.85f) {
                xVelocity *= 1.25f;
                yVelocity *= 0.75f;
                zVelocity *= 1.25f;
                var lightSpecs = SpiritLightSpecs.spiritLightSpecs(level, targetPos, cyclingSpiritType);
                lightSpecs.getBuilder()
                        .disableNoClip()
                        .multiplyLifetime(4)
                        .setGravityStrength(gravityStrength)
                        .multiplyLifetime(0.8f)
                        .setMotion(xVelocity, yVelocity, zVelocity)
                        .modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(2.5f));
                lightSpecs.getBloomBuilder()
                        .disableNoClip()
                        .multiplyLifetime(4)
                        .setGravityStrength(gravityStrength)
                        .setMotion(xVelocity, yVelocity, zVelocity)
                        .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(1.25f));
                lightSpecs.spawnParticles();
            }
        }
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            Vec3 offsetPosition = DataHelper.rotatingRadialOffset(targetPos, 0.6f, i, 8, gameTime, 160);
            Consumer<WorldParticleBuilder> behavior = b -> b.addTickActor(p -> {
                if (level.getGameTime() > gameTime + finalI * 4 && level.getGameTime() < gameTime + (finalI + 4) * 4) {
                    p.setParticleMotion(p.getParticleSpeed().add(0, 0.015f, 0));
                }
            });

            var lightSpecs = spiritLightSpecs(level, offsetPosition, activeSpiritType);
            lightSpecs.getBuilder().act(b -> b
                    .act(behavior)
                    .modifyColorData(d -> d.multiplyCoefficient(0.35f))
                    .modifyData(b::getScaleData, d -> d.multiplyValue(2f).multiplyCoefficient(0.9f))
                    .modifyData(b::getTransparencyData, d -> d.multiplyCoefficient(0.9f))
                    .multiplyLifetime(1.5f)
                    .setLifetime(b.getParticleOptions().lifetimeSupplier.get() + finalI * 2));
            lightSpecs.getBloomBuilder().act(b -> b
                    .act(behavior)
                    .modifyColorData(d -> d.multiplyCoefficient(0.35f))
                    .modifyData(b::getScaleData, d -> d.multiplyValue(1.6f).multiplyCoefficient(0.9f))
                    .modifyData(b::getTransparencyData, d -> d.multiplyCoefficient(0.9f))
                    .setLifetime((int) (b.getParticleOptions().lifetimeSupplier.get() + finalI * 2.5f)));
            lightSpecs.spawnParticles();
        }
    }

    public static void runewoodObeliskParticles(RunewoodObeliskBlockEntity obelisk, SpiritAltarBlockEntity altar, MalumSpiritType spiritType) {
        Level level = obelisk.getLevel();
        BlockPos obeliskPos = obelisk.getBlockPos();
        Vec3 startPos = obelisk.getParticleOffset().add(obeliskPos.getX(), obeliskPos.getY(), obeliskPos.getZ());
        spiritLightSpecs(level, startPos, spiritType).spawnParticles();
        if (level.getGameTime() % 2L == 0) {
            var random = level.random;
            long gameTime = level.getGameTime();
            BlockPos altarPos = altar.getBlockPos();
            Vec3 targetPos = altar.getCentralItemOffset().add(altarPos.getX(), altarPos.getY(), altarPos.getZ());
            Vec3 velocity = targetPos.subtract(startPos).normalize().scale(RandomHelper.randomBetween(random, 0.01f, 0.02f));
            double yOffset = Math.sin((gameTime % 360) / 30f) * 0.1f;
            Vec3 offsetPosition = DataHelper.rotatingRadialOffset(startPos.add(0, yOffset, 0), 0.45f, 0, 1, gameTime, 30);
            Consumer<WorldParticleBuilder> behavior = b -> b.addTickActor(p -> {
                if (gameTime % 6L == 0) {
                    p.setParticleMotion(p.getParticleSpeed().scale(1.05f));
                }
            });
            var lightSpecs = spiritLightSpecs(level, offsetPosition, spiritType);
            lightSpecs.getBuilder().act(b -> b
                    .act(behavior)
                    .setMotion(velocity)
                    .multiplyLifetime(2f)
                    .modifyData(b::getScaleData, d -> d.multiplyValue(RandomHelper.randomBetween(random, 1f, 2f))));
            lightSpecs.getBloomBuilder().act(b -> b
                    .act(behavior)
                    .setMotion(velocity)
                    .multiplyLifetime(1.5f)
                    .modifyData(b::getScaleData, d -> d.multiplyValue(RandomHelper.randomBetween(random, 0.6f, 1.5f))));
            lightSpecs.spawnParticles();
        }
    }
}