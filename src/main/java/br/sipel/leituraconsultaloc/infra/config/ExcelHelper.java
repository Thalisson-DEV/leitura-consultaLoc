package br.sipel.leituraconsultaloc.infra.config;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelHelper {

    // Lê uma célula como String, tratando nulos e tipos diferentes
    public static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Se for número, converte para String removendo .0 se for inteiro
            double val = cell.getNumericCellValue();
            if (val == (long) val) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (IllegalStateException e) {
                try {
                    double val = cell.getNumericCellValue();
                    return String.valueOf(val);
                } catch (Exception ex) {
                    return "";
                }
            }
        }

        return "";
    }

    // Converte para Double de forma segura
    public static Double parseDoubleSafe(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                String str = getStringCellValue(cell);
                if (str.isEmpty()) return null;
                return Double.parseDouble(str.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Converte para Integer de forma segura
    public static Integer parseIntegerSafe(Cell cell) {
        Double val = parseDoubleSafe(cell);
        return (val != null) ? val.intValue() : null;
    }
}