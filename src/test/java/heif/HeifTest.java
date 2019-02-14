package heif;

import com.nokia.heif.*;
import com.nokia.heif.Exception;
import org.junit.Test;

public class HeifTest {
    @Test
    public void readHeicFile() throws Exception {
        HEIF heif = new HEIF("photos/08C413643F0584C94AB549A15B4F67D26AEFB015119EF62D74D74E461FFBC678F316805909F7F43C41228AF3AFE591EC5ED0D5CBA6DF305B847245B6D3CB1DE8");
        GridImageItem gridImage = (GridImageItem) heif.getPrimaryImage();
        for (int row = 0; row < gridImage.getRowCount(); ++row) {
            for (int column = 0; column < gridImage.getColumnCount(); ++column) {
                HEVCImageItem tileImage = (HEVCImageItem) gridImage.getImage(column, row);
                System.out.println(str(tileImage.getSize()));
                System.out.println(tileImage.getItemDataAsArray().length);
            }
        }
    }

    private String str(Size size) {
        return size.width + "*" + size.height;
    }
}
