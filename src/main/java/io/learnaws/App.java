package io.learnaws;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

import io.nayuki.qrcodegen.QrCode;

public class App implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {

        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();

        // Add a default QR text
        String qrText = "Add your text after the /";
        // Remove the first slash (/) from the path
        String routePath = event.getRawPath().substring(1);

        // Update the QR text if route is not empty
        if (routePath.trim() != "") {
            // Decode the URI to look like 'cute bird' instead of 'cute%20bird'
            qrText = URLDecoder.decode(routePath, StandardCharsets.UTF_8);
        }
        try {
            // first we encode the text by passing text and error correction
            QrCode qr0 = QrCode.encodeText(qrText, QrCode.Ecc.MEDIUM);
            /*
             * // Code for renedering PNG image
             * BufferedImage img = QrUtils.toImage(qr0, 10, 1);
             * ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
             * ImageIO.write(img, "png", byteArr);
             * byte[] imgBytes = byteArr.toByteArray();
             * String base64 = Base64.getEncoder().encodeToString(imgBytes);
             */
            // using the utils we convert it to SVG or PNG
            String svg = QrUtils.toSvgString(qr0, 2, "#ffffff", "#000000");
            // set the SVG or png as string in response body
            response.setBody(svg);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // set it to true when sending PNG
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "image/svg+xml");
        // headers.put("Content-Type", "image/png");
        response.setHeaders(headers);
        return response;
    }

}