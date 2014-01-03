package fr.ybonnel.simpleweb4j.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * <p>This utility class provides an abstraction layer for sending multipart HTTP</p>
 * POST requests to a web server.
 *
 * @author www.codejava.net
 */
public class MultipartUtility {
    private static final String LINE_FEED = "\r\n";
    private final String boundary;
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param httpMethod method http.
     * @param requestURL request URL.
     * @throws IOException ioException.
     */
    public MultipartUtility(String httpMethod, String requestURL)
            throws IOException {
        this.charset = "UTF-8";

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod(httpMethod);
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in &lt;input type="file" name="..." /&gt;
     * @param uploadFile a File to be uploaded
     * @throws IOException ioException.
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     *         status OK, otherwise an exception is thrown.
     * @throws IOException ioException.
     */
    public SimpleWebTestUtil.UrlResponse response() throws IOException {
        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();

        SimpleWebTestUtil.UrlResponse response = new SimpleWebTestUtil.UrlResponse();
        response.status = httpConn.getResponseCode();
        response.headers = httpConn.getHeaderFields();
        response.contentType = httpConn.getContentType();

        if (response.status >= 400) {
            if (httpConn.getErrorStream() != null) {
                response.body = IOUtils.toString(httpConn.getErrorStream());
            }
        } else {
            if (httpConn.getInputStream() != null) {
                if (response.headers.containsKey("Content-Encoding")
                        && response.headers.get("Content-Encoding").get(0).equals("gzip")) {
                    response.body = IOUtils.toString(new GZIPInputStream(httpConn.getInputStream()));
                    response.isGzipped = true;
                } else {
                    response.body = IOUtils.toString(httpConn.getInputStream());
                }
            }
        }
        return response;
    }
}
