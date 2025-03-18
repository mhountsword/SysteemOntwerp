package nl.saxion.utils.readers;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.util.List;
public class CSVAdapter implements FileReader{
    @Override @SuppressWarnings("unchecked")
    public JSONArray readFile(String filePath) throws IOException {
        java.io.FileReader fileReader = new java.io.FileReader(filePath);
        CSVReader csvReader = new CSVReader(fileReader);
        try{
            List<String[]> records = csvReader.readAll();
            String[] headers = records.getFirst();
            JSONArray jsonArray = new JSONArray();

            for (int i = 1; i < records.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                String[] row = records.get(i);
                for (int j = 0; j < headers.length; j++) {
                    jsonObject.put(headers[j], row[j]);
                }
                jsonArray.add(jsonObject);
            }

            return jsonArray;
        } catch (CsvException e) {
            throw new IOException("Error reading CSV file");
        }
    }
}
