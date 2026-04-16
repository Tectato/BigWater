package bigwater.config;

import bigwater.BigWater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private EditBox scaleInput;
    private Checkbox overrideInput;
    private Button backButton;
    private Screen parent;

    protected ConfigScreen(Component title) {
        super(title);
        setup();
    }

    protected ConfigScreen(Minecraft minecraft, Font font, Component title) {
        super(minecraft, font, title);
        setup();
    }

    public ConfigScreen(Screen parent, Minecraft client, int width, int height) {
        super(Component.nullToEmpty("Big Water Config"));
        this.parent = parent;
        init(width, height);
        setup();
    }

    @Override
    public void onClose() {
        if (parent != null) {
            BigWater.setConfig(BigWater.VAR_DEFAULTSCALE, String.valueOf(Integer.valueOf(scaleInput.getValue())));
            BigWater.setConfig(BigWater.VAR_OVERRIDE, String.valueOf(overrideInput.selected()));
            BigWater.writeConfig();
            minecraft.setScreen(parent);
            minecraft.levelRenderer.allChanged();
        }
    }

    private void setup(){
        int posX = width / 2;
        int posY = 32;

        if (parent != null ) {
            backButton = Button.builder(CommonComponents.GUI_BACK, button -> onClose()).bounds(8,8,50,20).build();
        }
        addRenderableWidget(backButton);
        posY += 48;
        scaleInput = new EditBox(font, 64, 16, Component.literal("Standard texture scale"));
        scaleInput.setPosition(posX, posY);
        scaleInput.setValue(String.valueOf(BigWater.defaultTextureScale));
        addRenderableWidget(scaleInput);
        /*scaleInput.setResponder(s -> {
            try {
                int input = Integer.parseInt(s);
                BigWater.setConfig(BigWater.VAR_DEFAULTSCALE, String.valueOf(input));
            } catch (NumberFormatException e){
                scaleInput.setValue(String.valueOf(BigWater.defaultTextureScale));
            }
        });*/
        posY += 32;
        overrideInput = Checkbox.builder(Component.literal(""), font).pos(posX,posY).selected(BigWater.override).build();
        addRenderableWidget(overrideInput);
    }

    @Override
    public void render(final GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        //renderBackground(context, mouseX, mouseY, delta);
        backButton.render(graphics, mouseX, mouseY, delta);
        graphics.drawString(font, "Standard texture scale:", 32, scaleInput.getY() + 4, 0xFFFFFFFF);
        scaleInput.render(graphics, mouseX, mouseY, delta);
        graphics.drawString(font, "Override pack-provided settings:", 32, overrideInput.getY() + 4, 0xFFFFFFFF);
        overrideInput.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean isDoubled){
        if(backButton.mouseClicked(click, isDoubled)) return true;
        if(scaleInput.mouseClicked(click, isDoubled)){
            setFocused(scaleInput);
        }
        if(overrideInput.mouseClicked(click, isDoubled)){
            setFocused(overrideInput);
        }
        return super.mouseClicked(click, isDoubled);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if(scaleInput.charTyped(input)) return true;
        return super.charTyped(input);
    }
}
