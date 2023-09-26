import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationUtil {

    public static class Page<T> {
        private List<T> data;
        private int currentPage;
        private int totalPages;
        private int totalItems;

        public Page(List<T> data, int currentPage, int totalPages, int totalItems) {
            this.data = data;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }

        public List<T> getData() {
            return data;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }
    }

    public static <T> Page<T> paginate(List<T> list, int pageNumber, int pageSize) {
        int totalItems = list.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (pageNumber < 1 || pageNumber > totalPages) {
            throw new IllegalArgumentException("Invalid page number");
        }

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        if (fromIndex >= totalItems) {
            return new Page<>(List.of(), pageNumber, totalPages, totalItems);
        }

        List<T> pageData = list.subList(fromIndex, toIndex);

        return new Page<>(pageData, pageNumber, totalPages, totalItems);
    }

    public static int getTotalPages(List<?> list, int pageSize) {
        int totalItems = list.size();
        return (int) Math.ceil((double) totalItems / pageSize);
    }
}
