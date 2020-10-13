package zone.rong.lolilib.capability.temperature;

import net.dries007.tfc.api.capability.DumbStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zone.rong.lolilib.LoliLib;

import java.util.Timer;
import java.util.TimerTask;

@Mod.EventBusSubscriber
public class TemperatureCapability implements ICapabilitySerializable<NBTTagCompound>, ITemperatureHolder {

    public static final ResourceLocation KEY = new ResourceLocation(LoliLib.MOD_ID, "temperature");

    @CapabilityInject(ITemperatureHolder.class)
    public static final Capability<ITemperatureHolder> INSTANCE;

    static {
        INSTANCE = null;
    }

    public static void initCapability() {
        CapabilityManager.INSTANCE.register(ITemperatureHolder.class, new DumbStorage<>(), () -> null);
    }

    @SubscribeEvent
    public static void attachTemperatureCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            TemperatureCapability cap = new TemperatureCapability((EntityPlayer) event.getObject());
            event.addCapability(KEY, cap);
        }
    }

    private final EntityPlayer player;

    private float temperature;

    public TemperatureCapability(EntityPlayer player) {
        this.player = player;
        this.temperature = this.getAmbientTemperature();
        // prepareTasks();
    }

    private void prepareTasks() {
        Timer timer = new Timer("LoliLib/TemperatureMaintainer"); // TODO: tick callback?
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (player.world == null || player.world.getMinecraftServer() == null || player.isDead || !player.world.getMinecraftServer().isDedicatedServer() && Minecraft.getMinecraft().isGamePaused()) {
                    return;
                }
                BlockPos playerPos = player.getPosition();
                BlockPos pos1 = new BlockPos(playerPos.getX() - 9, playerPos.getY() - 3, playerPos.getZ() - 9);
                BlockPos pos2 = new BlockPos(playerPos.getX() + 9, playerPos.getY() + 5, playerPos.getZ() + 9);
                for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(pos1, pos2)) {
                    IBlockState state = player.world.getBlockState(pos);
                    if (state.getBlock() instanceof ITemperatureOwner) {
                        TemperatureCapability.this.temperature += ((ITemperatureOwner) state.getBlock()).getTemperatureDifference(state, player);
                    }
                }
                if (player.world.isDaytime()) {
                    TemperatureCapability.this.temperature += 0.5F;
                } else {
                    TemperatureCapability.this.temperature -= 0.5F;
                }
                if (player.inventory.hasItemStack(new ItemStack(Blocks.DIRT))) {
                    TemperatureCapability.this.temperature += 0.1F;
                }
            }
        }, 0, 5000);
    }

    public float getCurrentTemperature() {
        return temperature;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == INSTANCE;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == INSTANCE ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbtTagCompound) {

    }

}