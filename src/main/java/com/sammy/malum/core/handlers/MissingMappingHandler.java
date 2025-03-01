package com.sammy.malum.core.handlers;

import com.sammy.malum.MalumMod;
import com.sammy.malum.registry.common.block.BlockRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;

public class MissingMappingHandler {

    public static void correctMissingItemMappings(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            if (mapping.key.equals(MalumMod.malumPath("brilliance_cluster"))) {
                mapping.remap(ItemRegistry.CLUSTER_OF_BRILLIANCE.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("brilliance_chunk"))) {
                mapping.remap(ItemRegistry.CHUNK_OF_BRILLIANCE.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("soulstone_cluster"))) {
                mapping.remap(ItemRegistry.RAW_SOULSTONE.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("tainted_rock_pillar_cap"))) {
                mapping.remap(ItemRegistry.TAINTED_ROCK_COLUMN_CAP.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("tainted_rock_pillar"))) {
                mapping.remap(ItemRegistry.TAINTED_ROCK_COLUMN.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("twisted_rock_pillar_cap"))) {
                mapping.remap(ItemRegistry.TWISTED_ROCK_COLUMN_CAP.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("twisted_rock_pillar"))) {
                mapping.remap(ItemRegistry.TWISTED_ROCK_COLUMN.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("soulwood_sapling"))) {
                mapping.remap(ItemRegistry.SOULWOOD_GROWTH.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("ring_of_arcane_spoils"))) {
                mapping.remap(ItemRegistry.RING_OF_ESOTERIC_SPOILS.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("ring_of_prowess"))) {
                mapping.remap(ItemRegistry.RING_OF_ARCANE_PROWESS.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("blighted_spire"))) {
                mapping.remap(ItemRegistry.BLIGHTED_TUMOR.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("rare_earths"))) {
                mapping.remap(ItemRegistry.CTHONIC_GOLD.get());
            }

            if (mapping.key.equals(MalumMod.malumPath("block_of_rare_earths"))) {
                mapping.remap(ItemRegistry.BLOCK_OF_CTHONIC_GOLD.get());
            }
        }
    }

    public static void correctMissingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
            if (mapping.key.equals(MalumMod.malumPath("tainted_rock_pillar_cap"))) {
                mapping.remap(BlockRegistry.TAINTED_ROCK_COLUMN_CAP.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("tainted_rock_pillar"))) {
                mapping.remap(BlockRegistry.TAINTED_ROCK_COLUMN.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("twisted_rock_pillar_cap"))) {
                mapping.remap(BlockRegistry.TWISTED_ROCK_COLUMN_CAP.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("twisted_rock_pillar"))) {
                mapping.remap(BlockRegistry.TWISTED_ROCK_COLUMN.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("soulwood_sapling"))) {
                mapping.remap(BlockRegistry.SOULWOOD_GROWTH.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("blighted_spire"))) {
                mapping.remap(BlockRegistry.BLIGHTED_TUMOR.get());
            }
            if (mapping.key.equals(MalumMod.malumPath("block_of_rare_earths"))) {
                mapping.remap(BlockRegistry.BLOCK_OF_CTHONIC_GOLD.get());
            }
        }
    }
}