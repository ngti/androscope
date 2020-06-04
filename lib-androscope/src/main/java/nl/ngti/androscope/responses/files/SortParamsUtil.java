package nl.ngti.androscope.responses.files;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Comparator;

import kotlin.comparisons.ComparisonsKt;

/**
 * Using Kotlin for comparing primitive values seems quite inefficient, as it will enforce using
 * wrapper classes for this purpose. This will lead to a lot of extra allocations.
 * Or we would need to get rid of convenient lambdas to retrieve values of primitive fields from
 * {@link FileSystemEntry}.
 * <p>
 * But luckily Java supports lambdas also for primitive types :)
 */
final class SortParamsUtil {

    private SortParamsUtil() {
    }

    @NonNull
    static Comparator<FileSystemEntry> getFolderComparator() {
        // Folders should be shown first
        return (a, b) -> Boolean.compare(b.isFolder, a.isFolder);
    }

    @NonNull
    static Comparator<FileSystemEntry> getSortingComparator(@NonNull String order, @NonNull String column) {
        final Comparator<FileSystemEntry> nameComparator = compareStrings(FileSystemEntry::getName);
        final Comparator<FileSystemEntry> mainComparator;
        switch (column) {
            case "name":
                mainComparator = nameComparator;
                break;
            case "extension":
                mainComparator = compareStrings(FileSystemEntry::getExtension);
                break;
            case "date":
                mainComparator = compareLong(FileSystemEntry::getDateAsLong);
                break;
            case "size":
                mainComparator = compareLong(FileSystemEntry::getSizeAsLong);
                break;
            default:
                throw new IllegalArgumentException("Illegal sort column: " + column);
        }

        Comparator<FileSystemEntry> comparator;
        if (mainComparator != nameComparator) {
            comparator = ComparisonsKt.then(mainComparator, nameComparator);
        } else {
            comparator = mainComparator;
        }

        if ("desc".equals(order)) {
            comparator = Collections.reverseOrder(comparator);
        }

        return comparator;
    }

    @NonNull
    private static Comparator<FileSystemEntry> compareStrings(@NonNull StringSelector selector) {
        return (a, b) -> selector.get(a).compareToIgnoreCase(selector.get(b));
    }

    @NonNull
    private static Comparator<FileSystemEntry> compareLong(@NonNull LongSelector selector) {
        return (a, b) -> Long.compare(selector.get(a), selector.get(b));
    }

    @FunctionalInterface
    private interface StringSelector {

        @NonNull
        String get(@NonNull FileSystemEntry entry);
    }

    @FunctionalInterface
    private interface LongSelector {

        long get(@NonNull FileSystemEntry entry);
    }
}
