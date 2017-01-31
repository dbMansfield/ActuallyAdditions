/*
 * This file ("TileEntityFarmer.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import cofh.api.energy.EnergyStorage;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.farmer.IFarmerBehavior;
import de.ellpeck.actuallyadditions.api.internal.IFarmer;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class TileEntityFarmer extends TileEntityInventoryBase implements IFarmer {

    public static final int USE_PER_OPERATION = 1500;
    public final EnergyStorage storage = new EnergyStorage(100000);

    private int waitTime;
    private int checkX;
    private int checkY;

    private int lastEnergy;

    public TileEntityFarmer(){
        super(12, "farmer");
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type){
        super.writeSyncableNBT(compound, type);
        if(type != NBTType.SAVE_BLOCK){
            compound.setInteger("WaitTime", this.waitTime);
            compound.setInteger("CheckX", this.checkX);
            compound.setInteger("CheckY", this.checkY);
        }
        this.storage.writeToNBT(compound);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type){
        super.readSyncableNBT(compound, type);
        if(type != NBTType.SAVE_BLOCK){
            this.waitTime = compound.getInteger("WaitTime");
            this.checkX = compound.getInteger("CheckX");
            this.checkY = compound.getInteger("CheckY");
        }
        this.storage.readFromNBT(compound);
    }

    @Override
    public void updateEntity(){
        super.updateEntity();
        if(!this.worldObj.isRemote){
            if(!this.isRedstonePowered && this.storage.getEnergyStored() > 0){
                if(this.waitTime > 0){
                    this.waitTime--;

                    if(this.waitTime <= 0){
                        int radiusAroundCenter = 4;

                        IBlockState state = this.worldObj.getBlockState(this.pos);
                        int meta = state.getBlock().getMetaFromState(state);
                        // Convert from the internal metadata to the index used by EnumFacing
                        // i.e. apply the permutation (N,S,W,E) -> (S,W,N,E)
                        if(meta < 3) meta -= 1;
                        if(meta < 0) meta += 3;
                        BlockPos center = this.pos.offset(EnumFacing.getHorizontal(meta), radiusAroundCenter+1);

                        BlockPos query = center.add(this.checkX, 0, this.checkY);
                        this.checkBehaviors(query);

                        this.checkX++;
                        if(this.checkX > radiusAroundCenter){
                            this.checkX = -radiusAroundCenter;
                            this.checkY++;
                            if(this.checkY > radiusAroundCenter){
                                this.checkY = -radiusAroundCenter;
                            }
                        }
                    }
                }
                else{
                    this.waitTime = 5;
                }
            }

            if(this.lastEnergy != this.storage.getEnergyStored() && this.sendUpdateWithInterval()){
                this.lastEnergy = this.storage.getEnergyStored();
            }
        }
    }

    private void checkBehaviors(BlockPos query){
        for(IFarmerBehavior behavior : ActuallyAdditionsAPI.FARMER_BEHAVIORS){
            if(behavior.tryHarvestPlant(this.worldObj, query, this)){
                return;
            }
            else{
                for(int i = 0; i < this.slots.length; ++i){
                    ItemStack stack = this.slots[i];
                    if(StackUtil.isValid(stack)){
                        if(behavior.tryPlantSeed(stack, this.worldObj, query, this)){
                            /// @todo Do this properly
                            this.slots[i].stackSize -= 1;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return i < 6;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return slot >= 6;
    }

    @Override
    public EnumFacing getOrientation(){
        IBlockState state = this.worldObj.getBlockState(this.pos);
        return WorldUtil.getDirectionByPistonRotation(state.getBlock().getMetaFromState(state));
    }

    @Override
    public boolean addToSeedInventory(List<ItemStack> stacks, boolean actuallyDo){
        return WorldUtil.addToInventory(this, 0, 6, stacks, EnumFacing.UP, actuallyDo, true);
    }

    @Override
    public boolean addToOutputInventory(List<ItemStack> stacks, boolean actuallyDo){
        return WorldUtil.addToInventory(this, 6, 12, stacks, EnumFacing.UP, actuallyDo, true);
    }

    @Override
    public int getX(){
        return this.pos.getX();
    }

    @Override
    public int getY(){
        return this.pos.getY();
    }

    @Override
    public int getZ(){
        return this.pos.getZ();
    }

    @Override
    public World getWorldObject(){
        return this.worldObj;
    }

    @Override
    public void extractEnergy(int amount){
        this.storage.extractEnergy(amount, false);
    }

    @Override
    public int getEnergy(){
        return this.storage.getEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return this.storage.getMaxEnergyStored() ;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return this.storage.receiveEnergy(maxReceive, simulate);
    }
}
