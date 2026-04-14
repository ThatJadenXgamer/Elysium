package net.jadenxgamer.elysium_api.impl.compat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.jadenxgamer.elysium_api.impl.core.datadriven.brewing_recipe.ElysiumBrewingRecipe;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@EmiEntrypoint
public class ElysiumEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry emiRegistry) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();

        if (connection != null) {
            Registry<ElysiumBrewingRecipe> registry = connection.registryAccess()
                    .registry(ElysiumRegistries.BREWING_RECIPES)
                    .orElse(null);

            if (registry != null) {
                for (var entry : registry.entrySet()) {
                    ResourceLocation recipeId = entry.getKey().location();
                    ElysiumBrewingRecipe recipe = entry.getValue();

                    ItemStack inputItem = new ItemStack(recipe.inputItem());
                    if (!recipe.inputComponents().isEmpty()) inputItem.applyComponents(recipe.inputComponents());
                    ItemStack ingredientItem = new ItemStack(recipe.ingredient());
                    ItemStack outputItem = recipe.createResult(inputItem);

                    EmiStack inputStack = EmiStack.of(inputItem);
                    EmiIngredient ingredientStack = EmiStack.of(ingredientItem);
                    EmiStack outputStack = EmiStack.of(outputItem);

                    emiRegistry.addRecipe(new ElysiumEmiBrewingRecipe(inputStack, ingredientStack, outputStack, recipeId));
                }
            }
        }
    }
}