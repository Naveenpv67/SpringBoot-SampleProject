@Entity
@Table(name = "tpt_limit_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TptLimitAudit {

    @Id
    @Column(name = "ref_id", length = 60, nullable = false)
    private String refId;

    // Request Fetch Stage TPT Limit
    @Column(name = "req_fetch_tpt_limit_val", length = 50)
    private String reqFetchTptLimitVal;

    @Enumerated(EnumType.STRING)
    @Column(name = "req_fetch_tpt_limit_result", length = 50)
    private ResultEnum reqFetchTptLimitResult;

    @Column(name = "req_fetch_tpt_limit_ts")
    private Timestamp reqFetchTptLimitTimestamp;

    // Pay TPT Limit
    @Column(name = "pay_tpt_limit_val", length = 50)
    private String payTptLimitVal;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_tpt_limit_result", length = 50)
    private ResultEnum payTptLimitResult;

    @Column(name = "pay_tpt_limit_ts")
    private Timestamp payTptLimitTimestamp;
}
