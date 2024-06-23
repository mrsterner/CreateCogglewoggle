package dev.sterner.coggle

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.ItemProviderEntry
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.registry.CoggleItems
import dev.sterner.coggle.util.CogglePartials
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.ItemStack
import java.util.List


object Cogglewoggle : ModInitializer, ClientModInitializer {

    val REGISTRATE: CreateRegistrate = CreateRegistrate.create("coggle")
    var C_CREATIVE_TAB: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation("coggle"))

    override fun onInitialize() {
        CoggleBlocks.init()
        CoggleBlockEntityTypes.init()
        CoggleItems.init()
        REGISTRATE.register()

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            C_CREATIVE_TAB,
            createCreativeTab()
        );
    }

    override fun onInitializeClient() {
        CogglePartials.init();
    }

    fun asResource(s: String): ResourceLocation {
        return ResourceLocation("coggle", s)
    }

    fun createCreativeTab(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.coggle"))
            .icon { CoggleBlocks.DIFFERENTIAL.asStack() }
            .displayItems { _, output ->

                for (entry in REGISTRATE.getAll(Registries.ITEM)) {
                    output.accept(ItemStack(entry.get().asItem()))
                }

            }
            .build()
    }
}
