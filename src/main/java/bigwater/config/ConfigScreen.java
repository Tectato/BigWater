package bigwater.config;

import bigwater.BigWater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private EditBox scaleInput;
    private Button backButton;
    private Screen parent;

    protected ConfigScreen(Component title) {
        super(title);
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
            BigWater.writeConfig();
            minecraft.setScreen(parent);
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
        /*scaleInput.setResponder(s -> {
            try {
                int input = Integer.parseInt(s);
                BigWater.setConfig(BigWater.VAR_DEFAULTSCALE, String.valueOf(input));
            } catch (NumberFormatException e){
                scaleInput.setValue(String.valueOf(BigWater.defaultTextureScale));
            }
        });*/
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        backButton.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.text(font, "Standard texture scale:", 8, scaleInput.getY() + 4, 0xFFFFFFFF);
        scaleInput.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean isDoubled){
        if(backButton.mouseClicked(click, isDoubled)) return true;
        if(scaleInput.mouseClicked(click, isDoubled)){
            setFocused(scaleInput);
        }
        return super.mouseClicked(click, isDoubled);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if(scaleInput.charTyped(input)) return true;
        return super.charTyped(input);
    }
}
