String.format(
            "A GST Xelerator Monthly File already exists for %d-%02d. " +
            "Only one file per calendar month is allowed for settlement.",
            year, month
        )

        ALTER TABLE xelerator_monthly_file
ADD COLUMN year_month TEXT GENERATED ALWAYS AS (
    TO_CHAR(settlement_date, 'YYYY-MM')
) STORED;

@Column(name = "year_month", insertable = false, updatable = false)
private String yearMonth;

public void validateMonthlyFileDoesNotExist(int year, int month) {
    String yearMonth = String.format("%d-%02d", year, month);
    boolean exists = repository.existsByYearMonth(yearMonth);
    if (exists) {
        throw new MonthlyFileAlreadyExistsException(year, month);
    }
}


@Query("SELECT COUNT(f) > 0 FROM XeleratorMonthlyFile f WHERE f.yearMonth = :yearMonth")
boolean existsByYearMonth(@Param("yearMonth") String yearMonth);
