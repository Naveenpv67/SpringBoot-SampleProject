soap.envelope.start=<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
soap.header=<SOAP-ENV:Header>
soap.body.start=<SOAP-ENV:Body>
demat.landing.page.start=<dematLandingPage>
arg.start=<arg>
bank.code.start=<con:bankCode>
bank.code.end=</con:bankCode>
channel.start=<con:channel>
channel.end=</con:channel>
transacting.party.code.start=<con:transactingPartyCode>
transacting.party.code.end=</con:transactingPartyCode>
transaction.branch.start=<con:transactionBranch>
transaction.branch.end=</con:transactionBranch>
user.id.start=<con:userId>
user.id.end=</con:userId>
external.reference.no.start=<con:externalReferenceNo>
external.reference.no.end=</con:externalReferenceNo>
arg.end=</arg>
arg1.start=<arg1>
msghdr.start=<msgHdr>
msg.tp.start=<msg.tp>
msg.tp.end=</msg.tp>
reqapp.start=<req.app>
reqapp.end=</req.app>
reqtmstmp.start=<req.tmstmp>
reqtmstmp.end=</req.tmstmp>
msghdr.end=</msgHdr>
reqdtls.start=<reqDtls>
custid.start=<custid>
custid.end=</custid>
inclplgbal.start=<incl.plgbal>
inclplgbal.end=</incl.plgbal>
inclpval.start=<incl.pval>
inclpval.end=</incl.pval>
inclpvanal.start=<incl.pvanal>
inclpvanal.end=</incl.pvanal>
regrefno.start=<regrefno>
regrefno.end=</regrefno>
reqdtls.end=</reqDtls>
arg1.end=</arg1>
demat.landing.page.end=</dematLandingPage>
soap.body.end=</SOAP-ENV:Body>
soap.envelope.end=</SOAP-ENV:Envelope>
obp.demat.landing.page.bank.code=YourBankCode
obp.demat.landing.page.channel=YourChannel
obp.demat.landing.page.tx.party.cd=YourTxPartyCode
obp.demat.landing.page.tx.branch=YourTxBranch
obp.demat.landing.page.user.id=YourUserId
obp.demat.landing.page.msg.tp=YourMsgTp
obp.demat.landing.page.reqapp=YourReqApp
obp.demat.landing.page.reqtmstmp=YourReqTmsTmp
obp.demat.landing.page.inclplgbal=YourInclPlgBal
obp.demat.landing.page.inclpval=YourInclPval
obp.demat.landing.page.inclpvanal=YourInclPvanal



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DematProperties {

    @Value("${soap.envelope.start}")
    public String soapEnvelopeStart;

    @Value("${soap.header}")
    public String soapHeader;

    @Value("${soap.body.start}")
    public String soapBodyStart;

    @Value("${demat.landing.page.start}")
    public String dematLandingPageStart;

    @Value("${arg.start}")
    public String argStart;

    @Value("${bank.code.start}")
    public String bankCodeStart;

    @Value("${bank.code.end}")
    public String bankCodeEnd;

    @Value("${channel.start}")
    public String channelStart;

    @Value("${channel.end}")
    public String channelEnd;

    @Value("${transacting.party.code.start}")
    public String transactingPartyCodeStart;

    @Value("${transacting.party.code.end}")
    public String transactingPartyCodeEnd;

    @Value("${transaction.branch.start}")
    public String transactionBranchStart;

    @Value("${transaction.branch.end}")
    public String transactionBranchEnd;

    @Value("${user.id.start}")
    public String userIdStart;

    @Value("${user.id.end}")
    public String userIdEnd;

    @Value("${external.reference.no.start}")
    public String externalReferenceNoStart;

    @Value("${external.reference.no.end}")
    public String externalReferenceNoEnd;

    @Value("${arg.end}")
    public String argEnd;

    @Value("${arg1.start}")
    public String arg1Start;

    @Value("${msghdr.start}")
    public String msghdrStart;

    @Value("${msg.tp.start}")
    public String msgTpStart;

    @Value("${msg.tp.end}")
    public String msgTpEnd;

    @Value("${reqapp.start}")
    public String reqappStart;

    @Value("${reqapp.end}")
    public String reqappEnd;

    @Value("${reqtmstmp.start}")
    public String reqtmstmpStart;

    @Value("${reqtmstmp.end}")
    public String reqtmstmpEnd;

    @Value("${msghdr.end}")
    public String msghdrEnd;

    @Value("${reqdtls.start}")
    public String reqdtlsStart;

    @Value("${custid.start}")
    public String custidStart;

    @Value("${custid.end}")
    public String custidEnd;

    @Value("${inclplgbal.start}")
    public String inclplgbalStart;

    @Value("${inclplgbal.end}")
    public String inclplgbalEnd;

    @Value("${inclpval.start}")
    public String inclpvalStart;

    @Value("${inclpval.end}")
    public String inclpvalEnd;

    @Value("${inclpvanal.start}")
    public String inclpvanalStart;

    @Value("${inclpvanal.end}")
    public String inclpvanalEnd;

    @Value("${regrefno.start}")
    public String regrefnoStart;

    @Value("${regrefno.end}")
    public String regrefnoEnd;

    @Value("${reqdtls.end}")
    public String reqdtlsEnd;

    @Value("${arg1.end}")
    public String arg1End;

    @Value("${demat.landing.page.end}")
    public String dematLandingPageEnd;

    @Value("${soap.body.end}")
    public String soapBodyEnd;

    @Value("${soap.envelope.end}")
    public String soapEnvelopeEnd;

    @Value("${obp.demat.landing.page.bank.code}")
    public String obpDematLandingPageBankCode;

    @Value("${obp.demat.landing.page.channel}")
    public String obpDematLandingPageChannel;

    @Value("${obp.demat.landing.page.tx.party.cd}")
    public String obpDematLandingPageTxPartyCd;

    @Value("${obp.demat.landing.page.tx.branch}")
    public String obpDematLandingPageTxBranch;

    @Value("${obp.demat.landing.page.user.id}")
    public String obpDematLandingPageUserId;

    @Value("${obp.demat.landing.page.msg.tp}")
    public String obpDematLandingPageMsgTp;

    @Value("${obp.demat.landing.page.reqapp}")
    public String obpDematLandingPageReqapp;

    @Value("${obp.demat.landing.page.reqtmstmp}")
    public String obpDematLandingPageReqtmstmp;

    @Value("${obp.demat.landing.page.inclplgbal}")
    public String obpDematLandingPageInclplgbal;

    @Value("${obp.demat.landing.page.inclpval}")
    public String obpDematLandingPageInclpval;

    @Value("${obp.demat.landing.page.inclpvanal}")
    public String obpDematLandingPageInclpvanal;
}



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoapEnvelopeBuilder {

    @Autowired
    private DematProperties dematProperties;

    public String buildSoapEnvelope(String userId, String refNo) {
        StringBuilder soapEnvelope = new StringBuilder();

        try {
            soapEnvelope.append(dematProperties.soapEnvelopeStart)
                        .append(dematProperties.soapHeader)
                        .append(dematProperties.soapBodyStart)
                        .append(dematProperties.dematLandingPageStart)
                        .append(dematProperties.argStart)
                        .append(dematProperties.bankCodeStart)
                        .append(dematProperties.obpDematLandingPageBankCode)
                        .append(dematProperties.bankCodeEnd)
                        .append(dematProperties.channelStart)
                        .append(dematProperties.obpDematLandingPageChannel)
                        .append(dematProperties.channelEnd)
                        .append(dematProperties.transactingPartyCodeStart)
                        .append(dematProperties.obpDematLandingPageTxPartyCd)
                        .append(dematProperties.transactingPartyCodeEnd)
                        .append(dematProperties.transactionBranchStart)
                        .append(dematProperties.obpDematLandingPageTxBranch)
                        .append(dematProperties.transactionBranchEnd)
                        .append(dematProperties.userIdStart)
                        .append(dematProperties.obpDematLandingPageUserId)
                        .append(dematProperties.userIdEnd)
                        .append(dematProperties.externalReferenceNoStart)
                        .append(refNo)
                        .append(dematProperties.externalReferenceNoEnd)
                        .append(dematProperties.argEnd)
                        .append(dematProperties.arg1Start)
                        .append(dematProperties.msghdrStart)
                        .append(dematProperties.msgTpStart)
                        .append(dematProperties.obpDematLandingPageMsgTp)
                        .append(dematProperties.msgTpEnd)
                        .append(dematProperties.reqappStart)
                        .append(dematProperties.obpDematLandingPageReqapp)
                        .append(dematProperties.reqappEnd)
                        .append(dematProperties.reqtmstmpStart)
                        .append(dematProperties.obpDematLandingPageReqtmstmp)
                        .append(dematProperties.reqtmstmpEnd)
                        .append(dematProperties.msghdrEnd)
                        .append(dematProperties.reqdtlsStart)
                        .append(dematProperties.custidStart)
                        .append(userId)
                        .append(dematProperties.custidEnd)
                        .append(dematProperties.inclplgbalStart)
                        .append(dematProperties.obpDematLandingPageInclplgbal)
                        .append(dematProperties.inclplgbalEnd)
                        .append(dematProperties.inclpvalStart)
                        .append(dematProperties.obpDematLandingPageInclpval)
                        .append(dematProperties.inclpvalEnd)
                        .append(dematProperties.inclpvanalStart)
                        .append(dematProperties.obpDematLandingPageInclpvanal)
                        .append(dematProperties.inclpvanalEnd)
                        .append(dematProperties.regrefnoStart)
                        .append(refNo)
                        .append(dematProperties.regrefnoEnd)
                        .append(dematProperties.reqdtlsEnd)
                        .append(dematProperties.arg1End)
                        .append(dematProperties.dematLandingPageEnd)
                        .append(dematProperties.soapBodyEnd)
                        .append(dematProperties.soapEnvelopeEnd);

        } catch (JSONException je) {
            // Handle exception if necessary
        }

        return soapEnvelope.toString();
    }
}
