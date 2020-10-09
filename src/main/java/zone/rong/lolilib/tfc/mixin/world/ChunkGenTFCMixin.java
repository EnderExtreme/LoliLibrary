package zone.rong.lolilib.tfc.mixin.world;

import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ChunkGenTFC.class)
public abstract class ChunkGenTFCMixin implements IChunkGenerator {

    @Shadow(remap = false) @Final private MapGenBase riverRavineGen;
    @Shadow(remap = false) @Final private World world;
    @Unique private MapGenVillage villageGenerator = new MapGenVillage();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectCtor(World w, String settingsString, CallbackInfo ci) {
        this.villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(this.villageGenerator, InitMapGenEvent.EventType.VILLAGE);
    }

    @Redirect(method = "generateChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/MapGenBase;generate(Lnet/minecraft/world/World;IILnet/minecraft/world/chunk/ChunkPrimer;)V"))
    private void injectVillageGeneration(MapGenBase mapGenBase, World world, int x, int z, ChunkPrimer primer) {
        this.riverRavineGen.generate(world, x, z, primer);
        this.villageGenerator.generate(world, x, z, primer);
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onChunkPopulate(ZLnet/minecraft/world/gen/IChunkGenerator;Lnet/minecraft/world/World;Ljava/util/Random;IIZ)V", ordinal = 1, remap = false))
    private void injectVillagePopulation(boolean pre, IChunkGenerator gen, World world, Random rand, int x, int z, boolean hasVillageGenerated) {
        ForgeEventFactory.onChunkPopulate(false, gen, world, rand, x, z, false);
        this.villageGenerator.generateStructure(world, rand, new ChunkPos(x, z));
    }

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
        if (structureName.equals("Village")) {
            return this.villageGenerator.isInsideStructure(pos);
        }
        return false;
    }

    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored) {
        if (structureName.equals("Village")) {
            return this.villageGenerator.getNearestStructurePos(world, pos, findUnexplored);
        }
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        this.villageGenerator.generate(this.world, x, z, null);
    }

}
