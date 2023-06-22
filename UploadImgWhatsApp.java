import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author renato
 */
public class UploadImgWhatsApp {

    private final String url;
    private final String accessToken;
    private final String type;
    private final String messagingProduct;

    public UploadImgWhatsApp(String url, String accessToken, String type, String messagingProduct) {
        this.url = url;
        this.accessToken = accessToken;
        this.type = type;
        this.messagingProduct = messagingProduct;

    }

    public String uploadImg(String filePath) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "multipart/form-data");

        // criando  form data
        String boundary = "qualquercoisaesperoquedecerto";
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

        // preenchendo o campo type
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"type\"\r\n\r\n");
        writer.append(type).append("\r\n");

        // preenchendo o canmpo messagingProduct
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"messaging_product\"\r\n\r\n");
        writer.append(messagingProduct).append("\r\n");

        // preenchendo o campo file
        File file = new File(filePath);
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
        writer.append("Content-Type: ").append(type).append("\r\n\r\n");
        writer.flush();

        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append("\r\n");
        writer.append("--").append(boundary).append("--\r\n");
        writer.flush();
        writer.close();

        // Get the response
        int responseCode = conn.getResponseCode();

        System.out.println("Response Code: " + responseCode);

        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println("Response from API: " + response.toString());

        return response.toString();
    }
}
