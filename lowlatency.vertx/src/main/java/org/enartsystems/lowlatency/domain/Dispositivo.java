/**
 * 
 */
package org.enartsystems.lowlatency.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author manuel
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Dispositivo {
	@CsvBindByName(column = "FECHA_HORA_MINUTO")
	@CsvDate("yyyy-MM-dd HH:mm:ss") //2019-03-18 0:00:00
	Date fecha;
	@CsvBindByName(column = "DISPOSITIVO")
	String nombre;
	@CsvBindByName(column = "VISITAS")
	@CsvNumber("###.###")
	int visitas;
	@CsvBindByName(column = "UNIDADES_VENDIDAS")
	@CsvNumber("###.###")
	long unidades;
	@CsvBindByName(column = "IMPORTE")
	@CsvNumber("###.###")
	BigDecimal importe;

}
