package ellpeck.actuallyadditions.tile;

import ellpeck.actuallyadditions.config.values.ConfigIntValues;
import ellpeck.actuallyadditions.items.InitItems;
import ellpeck.actuallyadditions.items.ItemFertilizer;
import ellpeck.actuallyadditions.items.ItemMisc;
import ellpeck.actuallyadditions.items.metalists.TheMiscItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCompost extends TileEntityInventoryBase{

    public final int amountNeededToConvert = ConfigIntValues.COMPOST_AMOUNT.getValue();
    public final int conversionTimeNeeded = ConfigIntValues.COMPOST_TIME.getValue();

    public int conversionTime;

    public TileEntityCompost(){
        super(1, "compost");
    }

    @Override
    public void updateEntity(){
        if(!worldObj.isRemote){

            if(this.slots[0] != null && this.slots[0].stackSize > 0){
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.slots[0].stackSize + (this.slots[0].getItem() instanceof ItemFertilizer ? 1 : 0), 2);
            }
            else worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);

            boolean theFlag = this.conversionTime > 0;
            if(this.slots[0] != null && !(this.slots[0].getItem() instanceof ItemFertilizer) && this.slots[0].stackSize >= this.amountNeededToConvert){
                this.conversionTime++;
                if(this.conversionTime >= this.conversionTimeNeeded){
                    this.slots[0] = new ItemStack(InitItems.itemFertilizer, this.amountNeededToConvert);
                    this.conversionTime = 0;
                }
            }
            if(theFlag != this.conversionTime > 0){
                this.markDirty();
            }
        }
    }

    @Override
    public int getInventoryStackLimit(){
        return this.amountNeededToConvert;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        compound.setInteger("ConversionTime", this.conversionTime);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
        this.conversionTime = compound.getInteger("ConversionTime");
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return stack.getItem() instanceof ItemMisc && stack.getItemDamage() == TheMiscItems.MASHED_FOOD.ordinal();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side){
        return stack.getItem() instanceof ItemFertilizer;
    }
}