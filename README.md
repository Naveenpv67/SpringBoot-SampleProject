public class Mapper {

    public static NBBLTxnDataAE mapSourceToTarget(NBBLReqPayTransactionDataTable source) {
        NBBLTxnDataAE target = new NBBLTxnDataAE();
        
        target.setRefId(source.getReferenceId());
        target.setId(source.getId());
        target.setAckRespJson(source.getAckResponseJson());
        target.setMsgId(source.getMsgId());
        target.setOrgId(source.getOrgId());
        target.setCorrelKey(source.getCorrelationKey());
        target.setNbblMerchid(source.getNbbMerchantId());
        target.setPald(source.getPald());
        target.setReqTs(source.getRequestTs());
        target.setAmount(source.getAmount());
        target.setCurrency(source.getCurrency());
        target.setNbblPayload(source.getNbblPayload());
        target.setNbblRespTs(source.getNbblResponseTs());
        target.setErrCode(source.getErrCode());
        target.setErrReason(source.getErrReason());
        target.setReqJson(source.getRequestJson());
        target.setPayload(source.getPayload());
        target.setRespStatus(source.getResponseStatus());
        target.setResult(source.getResult());

        return target;
    }
}
