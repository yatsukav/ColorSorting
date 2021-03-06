package com.yatsukav.colorsort.sorts;

/**
 * Bubble sort is a simple sorting algorithm that works by repeatedly stepping
 * through the list to be sorted, comparing each pair of adjacent items and
 * swapping them if they are in the wrong order. The pass through the list is
 * repeated until no swaps are needed, which indicates that the list is sorted.
 * <p>
 * Family: Exchanging.
 * Space: In-place.
 * Stable: True.
 * <p>
 * Average case = O(n^2) Worst case = O(n^2) Best case = O(n)
 * <p>
 * http://en.wikipedia.org/wiki/Bubble_sort
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class ImageBubbleSorter extends ImageSorter {

    @Override
    protected void sort(int[] unsorted, boolean withPersisting, int persistStep) {
        maxOutputImages = 0;
        boolean swapped = true;
        int length = unsorted.length;
        while (swapped) {
            swapped = false;
            for (int i = 1; i < length; i++) {
                if (unsorted[i] < unsorted[i - 1]) {
                    swap(i, i - 1, unsorted);
                    swapped = true;
                    maxOutputImages++;
                    if (withPersisting && maxOutputImages % persistStep == 0) persistStep(unsorted);
                }
            }
            length--;
        }
    }

    private void swap(int index1, int index2, int[] unsorted) {
        int value = unsorted[index1];
        unsorted[index1] = unsorted[index2];
        unsorted[index2] = value;
    }
}