package bigwater.config;

import bigwater.BigWater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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

    public ConfigScreen(Screen parent, Minecraft client, int width, int height) {
        super(Component.nullToEmpty("Big Water Config"));
        this.parent = parent;
        init(client, width, height);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(backButton.mouseClicked(mouseX, mouseY, button)) return true;
        if(scaleInput.mouseClicked(mouseX, mouseY, button)){
            setFocused(scaleInput);
        }
        if(overrideInput.mouseClicked(mouseX, mouseY, button)){
            setFocused(overrideInput);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if(scaleInput.charTyped(c, modifiers)) return true;
        return super.charTyped(c, modifiers);
    }
}
