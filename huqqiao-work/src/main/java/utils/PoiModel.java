package utils;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/10 16:14
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class PoiModel {

    private String content;

    private String oldContent;

    private int rowIndex;

    private int cellIndex;

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }
}
