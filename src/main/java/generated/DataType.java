//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.12 at 09:55:25 AM AST 
//


package generated;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ColumnNames" type="{}ColumnNamesType"/&gt;
 *         &lt;element name="DataRows" type="{}DataRowsType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder = {
    "columnNames",
    "dataRows"
})
public class DataType
    implements Serializable
{

    private final static long serialVersionUID = -1L;
    @XmlElement(name = "ColumnNames", required = true)
    protected ColumnNamesType columnNames;
    @XmlElement(name = "DataRows", required = true)
    protected DataRowsType dataRows;

    /**
     * Gets the value of the columnNames property.
     * 
     * @return
     *     possible object is
     *     {@link ColumnNamesType }
     *     
     */
    public ColumnNamesType getColumnNames() {
        return columnNames;
    }

    /**
     * Sets the value of the columnNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColumnNamesType }
     *     
     */
    public void setColumnNames(ColumnNamesType value) {
        this.columnNames = value;
    }

    /**
     * Gets the value of the dataRows property.
     * 
     * @return
     *     possible object is
     *     {@link DataRowsType }
     *     
     */
    public DataRowsType getDataRows() {
        return dataRows;
    }

    /**
     * Sets the value of the dataRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRowsType }
     *     
     */
    public void setDataRows(DataRowsType value) {
        this.dataRows = value;
    }

}