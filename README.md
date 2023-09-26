import java.util.List;
import java.util.stream.Collectors;

public class PaginationUtil {

    public static <T> List<T> paginateList(List<T> list, int pageNumber, int pageSize) {
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        
        if (fromIndex >= list.size()) {
            return List.of();
        }
        
        return list.subList(fromIndex, toIndex);
    }
}
