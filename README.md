public interface XeleratorMonthlyFileRepository extends JpaRepository<XeleratorMonthlyFile, Long> {

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM xelerator_monthly_file
        WHERE EXTRACT(YEAR FROM settlement_date) = :year
          AND EXTRACT(MONTH FROM settlement_date) = :month
        """, nativeQuery = true)
    boolean existsByYearAndMonth(@Param("year") int year, @Param("month") int month);
}

@Service
public class XeleratorMonthlyFileService {

    @Autowired
    private XeleratorMonthlyFileRepository repository;

    public void saveFile(XeleratorMonthlyFile file) {
        OffsetDateTime settlementDate = file.getSettlementDate();
        int year = settlementDate.getYear();
        int month = settlementDate.getMonthValue();

        boolean exists = repository.existsByYearAndMonth(year, month);
        if (exists) {
            throw new IllegalArgumentException(
                String.format("A file already exists for %d-%02d", year, month)
            );
        }

        repository.save(file);
    }
}

CREATE UNIQUE INDEX ux_monthly_file_year_month
ON xelerator_monthly_file (
    EXTRACT(YEAR FROM settlement_date),
    EXTRACT(MONTH FROM settlement_date)
);
