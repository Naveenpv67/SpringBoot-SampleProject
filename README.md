// Fetch the set names for the given namespace using Info.request
String response = Info.request(client.getNodes()[0], "sets");
List<String> setNames = new ArrayList<>();

String[] setsInfo = response.split(";");
for (String setInfo : setsInfo) {
    String setName = setInfo.split(":")[0].trim();
    setNames.add(setName);
}
