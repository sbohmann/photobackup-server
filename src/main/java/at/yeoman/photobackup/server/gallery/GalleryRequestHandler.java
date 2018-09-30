package at.yeoman.photobackup.server.gallery;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
public class GalleryRequestHandler {
    private static final String JsJodaPath = "/js-joda/";
    private static final String JsJodaMappingPath = JsJodaPath + "**";
    private static final String JsJodaResourcePath = "/META-INF/resources/webjars/js-joda/1.8.2/dist/";

    @GetMapping(value = { "/gallery", "/gallery/{date}" })
    public String gallery(Model model, @PathVariable(required = false) String date) {
        if (date != null) {
            model.addAttribute("date", LocalDate.parse(date));
        } else {
            model.addAttribute("date", "any");
        }
        return "gallery/gallery";
    }

    @GetMapping(JsJodaMappingPath)
    @ResponseBody
    public Resource getJsJodaScript(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getServletPath();
        String resourcePath = requestPath.replace(JsJodaPath, JsJodaResourcePath);
        InputStream in = getClass().getResourceAsStream(resourcePath);
        if (requestPath.endsWith(".js")) {
            response.setContentType("application/javascript");
        }
        return new ClassPathResource(resourcePath);
    }
}
