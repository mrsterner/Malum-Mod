package com.sammy.malum.registry.common.item;

import com.sammy.malum.MalumMod;
import com.sammy.malum.client.renderer.curio.TokenOfGratitudeRenderer;
import com.sammy.malum.client.renderer.curio.TopHatCurioRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = MalumMod.MALUM, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CurioRegistry {

    @Mod.EventBusSubscriber(modid = MalumMod.MALUM, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientOnly {
        @SubscribeEvent
        public static void registerCurioRenderers(FMLClientSetupEvent event) {
            CuriosRendererRegistry.register(ItemRegistry.TOKEN_OF_GRATITUDE.get(), TokenOfGratitudeRenderer::new);
            CuriosRendererRegistry.register(ItemRegistry.TOPHAT.get(), TopHatCurioRenderer::new);
        }
    }
}