package me.panpf.sketch.zoom.block;

import androidx.annotation.NonNull;

import java.util.List;

import me.panpf.sketch.SketchException;

public class BlockSortException extends SketchException {
    @NonNull
    private List<Block> blockList;
    private boolean useLegacyMergeSort;

    public BlockSortException(@NonNull IllegalArgumentException cause, @NonNull List<Block> blockList, boolean useLegacyMergeSort) {
        super(cause);
        this.blockList = blockList;
        this.useLegacyMergeSort = useLegacyMergeSort;
    }

    @NonNull
    @Override
    public synchronized IllegalArgumentException getCause() {
        return (IllegalArgumentException) super.getCause();
    }

    @NonNull
    public List<Block> getBlockList() {
        return blockList;
    }

    public boolean isUseLegacyMergeSort() {
        return useLegacyMergeSort;
    }
}
