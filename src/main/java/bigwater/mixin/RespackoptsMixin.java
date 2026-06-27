package bigwater.mixin;

import io.gitlab.jfronny.respackopts.mixin.ResourcePackManagerMixin;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ResourcePackManagerMixin.class)
public class RespackoptsMixin {

    @Inject(
            at = @At(
                    value = "HEAD"
            ),
            method = "Lio/gitlab/jfronny/respackopts/mixin/ResourcePackManagerMixin;rpo$resolveDataLocation(Lnet/minecraft/server/packs/PackResources;)Ljava/nio/file/Path;"
    )
    public static void resolveDataLocationMixin(PackResources rpi, CallbackInfoReturnable<Path> cir){
        System.out.println("[RPO] Pack ID: " + rpi.packId());
    }
}
