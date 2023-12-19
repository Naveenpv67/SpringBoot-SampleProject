while (recordSet.next()) {
    Map<String, Object> data = new HashMap<>();

    Key key = recordSet.getKey();
    if (key != null && key.userKey != null) {
        data.put("key", key.userKey.getObject());
    }

    data.put("set_name", setName);

    Record record = recordSet.getRecord();
    if (record != null && record.bins != null) {
        Map<String, Object> bins = record.bins;
        data.putAll(bins);
    }

    result.add(data);
}
