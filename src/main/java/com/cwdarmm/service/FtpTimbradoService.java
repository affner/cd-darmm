package com.cwdarmm.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para manejar el flujo de timbrado vía FTP y la verificación
 * posterior en la base de datos.
 *
 * <p>Contiene un esqueleto que, una vez que el archivo ha sido colocado
 * exitosamente vía FTP, realiza una consulta periódica a la tabla de
 * comprobantes para verificar la existencia de timbrados con una serie
 * determinada.</p>
 */
@Service
@RequiredArgsConstructor
public class FtpTimbradoService {

    private static final Logger log = LoggerFactory.getLogger(FtpTimbradoService.class);
    private final JdbcTemplate jdbcTemplate;

    /**
     * Deposita el archivo vía FTP y, si la operación es exitosa, inicia un
     * proceso de verificación periódico en la base de datos.
     *
     * @param idProd identificador del producto que se relacionará con la serie
     *               del comprobante.
     */
    public void depositAndVerify(String idProd) {
        // TODO: implementar la lógica de colocación del archivo vía FTP.
        // Se asume que la operación fue exitosa y se inicia la verificación.

        pollComprobantesBySerie(idProd);
    }

    /**
     * Realiza una consulta a la tabla 'comprobantes' cada 10 segundos para
     * verificar si existe un timbrado cuya serie coincida con el idProd
     * proporcionado.
     *
     * @param serie identificador ligado al registro del comprobante.
     */
    private void pollComprobantesBySerie(String serie) {
        String sql = "SELECT COUNT(*) FROM comprobantes WHERE serie = ?";
        boolean encontrado = false;

        while (!encontrado) {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serie);
            if (count != null && count > 0) {
                log.info("Comprobante con serie {} encontrado", serie);
                encontrado = true;
            } else {
                try {
                    Thread.sleep(10_000L); // espera 10 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Verificación interrumpida para la serie {}", serie);
                    return;
                }
            }
        }
    }
}

