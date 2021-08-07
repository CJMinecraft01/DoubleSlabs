package cjminecraft.doubleslabs.api;

public interface IStateContainer {

    IBlockInfo getPositiveBlockInfo();

    IBlockInfo getNegativeBlockInfo();

    void markDirty();

}
