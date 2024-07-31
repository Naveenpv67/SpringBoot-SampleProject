public class PaginationUtil {

    public static <T> List<T> paginateAndSort(List<T> data, int pageNo, int pageSize, String sortField, String sortDirection) {
        // Calculate the number of elements to skip
        long skip = (long) (pageNo - 1) * pageSize;
        
        // Determine the limit
        int limit = pageSize;

        // Create a comparator based on the sort field and direction
        Comparator<T> comparator = getComparator(sortField, sortDirection);

        // Create a stream and optionally sort it if a comparator is provided
        return (comparator == null ? data.parallelStream() : data.parallelStream().sorted(comparator))
                .skip(skip)    // Skip the first 'skip' elements
                .limit(limit)  // Limit the number of elements to 'limit'
                .collect(Collectors.toList()); // Collect the results into a list
    }

    private static <T> Comparator<T> getComparator(String sortField, String sortDirection) {
        return (o1, o2) -> {
            try {
                Field field = o1.getClass().getDeclaredField(sortField);
                field.setAccessible(true);
                Comparable value1 = (Comparable) field.get(o1);
                Comparable value2 = (Comparable) field.get(o2);

                return "desc".equalsIgnoreCase(sortDirection) 
                        ? value2.compareTo(value1) 
                        : value1.compareTo(value2);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to sort by field: " + sortField, e);
            }
        };
    }
