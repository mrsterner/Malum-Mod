package com.sammy.malum.common.item.curiosities.curios.soulward;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sammy.malum.common.capability.MalumPlayerDataCapability;
import com.sammy.malum.common.item.curiosities.curios.*;
import com.sammy.malum.core.handlers.SoulWardHandler;
import com.sammy.malum.registry.common.AttributeRegistry;
import com.sammy.malum.core.systems.item.IMalumEventResponderItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import top.theillusivec4.curios.api.*;

import java.util.UUID;

public class CurioMagebaneBelt extends MalumCurioItem implements IMalumEventResponderItem {

    public CurioMagebaneBelt(Properties builder) {
        super(builder, MalumTrinketType.ORNATE);
    }

    @Override
    public void onSoulwardAbsorbDamage(LivingHurtEvent event, Player wardedPlayer, ItemStack stack, float soulwardLost, float damageAbsorbed) {
        DamageSource source = event.getSource();
        if (source.getEntity() != null) {
            if (true) {
                if (source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.THORNS)) {
                    SoulWardHandler handler = MalumPlayerDataCapability.getCapability(wardedPlayer).soulWardHandler;
                    handler.soulWardProgress = 0;
                }
            }
        }
    }

    @Override
    public void addAttributeModifiers(Multimap<Attribute, AttributeModifier> map, SlotContext slotContext, ItemStack stack) {
        addAttributeModifier(map, AttributeRegistry.SOUL_WARD_RECOVERY_SPEED.get(), uuid -> new AttributeModifier(uuid,
                "Curio Soul Ward Recovery Speed", 3f, AttributeModifier.Operation.ADDITION));
        addAttributeModifier(map, AttributeRegistry.SOUL_WARD_CAP.get(), uuid -> new AttributeModifier(uuid,
                "Curio Soul Ward Capacity", 6f, AttributeModifier.Operation.ADDITION));
    }
}