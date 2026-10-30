package com.bovedadocs.store;

import com.bovedadocs.model.Classification;
import com.bovedadocs.model.Document;
import com.bovedadocs.model.Role;
import com.bovedadocs.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SeedData {
    private SeedData() {
    }

    public static Map<String, User> users() {
        Map<String, User> users = new LinkedHashMap<>();
        users.put("u1", new User("u1", "Laura Gómez", "Legal", Role.VIEWER));
        users.put("u2", new User("u2", "Andrés Rojas", "Legal", Role.EDITOR));
        users.put("u3", new User("u3", "Sofía Pérez", "RRHH", Role.EDITOR));
        users.put("u4", new User("u4", "Carlos Ruiz", "Dirección", Role.ADMIN));
        return users;
    }

    public static Map<String, Document> documents() {
        Map<String, Document> documents = new LinkedHashMap<>();
        documents.put("d1", new Document(
                "d1", "Contrato marco 2026", "Legal", "u2", "Andrés Rojas",
                Classification.PUBLIC, 1240,
                "CONTRATO MARCO DE PRESTACIÓN DE SERVICIOS\n\n"
                        + "Entre BÓVEDA S.A.S., representada por su gerente general, y la firma consultora "
                        + "NORTE LEGAL S.A.S., se celebra el presente contrato marco.\n\n"
                        + "CLÁUSULA 1. OBJETO\n"
                        + "El contratista prestará servicios de asesoría jurídica preventiva, revisión de "
                        + "contratos y acompañamiento en procesos administrativos.\n\n"
                        + "CLÁUSULA 2. PLAZO\n"
                        + "El contrato tendrá una vigencia de doce (12) meses contados desde su firma, "
                        + "prorrogables por mutuo acuerdo.\n\n"
                        + "CLÁUSULA 3. VALOR\n"
                        + "El valor mensual asciende a cuarenta y ocho millones de pesos (COP 48.000.000), "
                        + "pagaderos dentro de los treinta días siguientes a la radicación de la factura.\n\n"
                        + "CLÁUSULA 4. CONFIDENCIALIDAD\n"
                        + "Las partes se obligan a mantener reserva sobre la información intercambiada.\n\n"
                        + "Firmado en Bogotá D.C., a los quince días del mes de enero de 2026."
        ));
        documents.put("d2", new Document(
                "d2", "Demanda laboral", "Legal", "u2", "Andrés Rojas",
                Classification.CONFIDENTIAL, 860,
                "MEMORANDO JURÍDICO — DEMANDA LABORAL\n\n"
                        + "Radicado: 2026-00417\n"
                        + "Demandante: exfuncionario del área de operaciones.\n"
                        + "Demandado: Bóveda S.A.S.\n\n"
                        + "HECHOS RELEVANTES\n"
                        + "1. El demandante alega terminación sin justa causa.\n"
                        + "2. Solicita indemnización, intereses y costas procesales.\n"
                        + "3. La audiencia inicial fue programada para el 18 de noviembre de 2026.\n\n"
                        + "ESTRATEGIA\n"
                        + "Se recomienda conciliar antes de la audiencia y revisar los soportes de pago "
                        + "de las obligaciones laborales del último año.\n\n"
                        + "Este documento está clasificado como confidencial y no debe circular fuera "
                        + "del comité jurídico."
        ));
        documents.put("d3", new Document(
                "d3", "Nómina octubre", "RRHH", "u3", "Sofía Pérez",
                Classification.CONFIDENTIAL, 420,
                "RESUMEN DE NÓMINA — OCTUBRE 2026\n\n"
                        + "Empleados activos: 148\n"
                        + "Total devengado: COP 1.842.600.000\n"
                        + "Deducciones de ley: COP 214.300.000\n"
                        + "Neto a pagar: COP 1.628.300.000\n\n"
                        + "NOVEDADES\n"
                        + "• Tres ingresos nuevos en el área comercial.\n"
                        + "• Dos retiros voluntarios en operaciones.\n"
                        + "• Una licencia de maternidad activa.\n\n"
                        + "La información individual de cada colaborador se conserva en el anexo "
                        + "cifrado y solo puede ser consultada por el área de RRHH."
        ));
        documents.put("d4", new Document(
                "d4", "Manual de convivencia", "RRHH", "u3", "Sofía Pérez",
                Classification.INTERNAL, 980,
                "MANUAL DE CONVIVENCIA LABORAL\n\n"
                        + "1. PROPÓSITO\n"
                        + "Establecer las normas de comportamiento y los canales de atención para "
                        + "garantizar un ambiente de trabajo respetuoso.\n\n"
                        + "2. DERECHOS Y DEBERES\n"
                        + "Todo colaborador tiene derecho a recibir trato digno, capacitación y "
                        + "retroalimentación periódica. Así mismo, debe cumplir los protocolos de "
                        + "seguridad y reportar situaciones de riesgo.\n\n"
                        + "3. RUTA DE ATENCIÓN\n"
                        + "Las situaciones de conflicto se reportan al líder inmediato y, si persisten, "
                        + "al comité de convivencia.\n\n"
                        + "4. SANCIONES\n"
                        + "El incumplimiento de las normas dará lugar a un proceso disciplinario "
                        + "conforme al reglamento interno."
        ));
        documents.put("d5", new Document(
                "d5", "Presupuesto Q4", "Operaciones", "u4", "Carlos Ruiz",
                Classification.INTERNAL, 1500,
                "PRESUPUESTO OPERATIVO — CUARTO TRIMESTRE 2026\n\n"
                        + "Ingresos proyectados: COP 6.420.000.000\n"
                        + "Costos directos: COP 3.180.000.000\n"
                        + "Gastos administrativos: COP 1.240.000.000\n"
                        + "Margen esperado: 22,4 %\n\n"
                        + "INVERSIONES\n"
                        + "• Renovación de infraestructura tecnológica: COP 380.000.000.\n"
                        + "• Plan de mantenimiento preventivo: COP 120.000.000.\n"
                        + "• Capacitación del equipo operativo: COP 45.000.000.\n\n"
                        + "RIESGOS\n"
                        + "El alza de precios de proveedores logísticos podría reducir el margen en "
                        + "dos puntos porcentuales."
        ));
        documents.put("d6", new Document(
                "d6", "Acta de junta", "Dirección", "u4", "Carlos Ruiz",
                Classification.CONFIDENTIAL, 2100,
                "ACTA N.º 14 — JUNTA DIRECTIVA\n\n"
                        + "Fecha: 22 de octubre de 2026\n"
                        + "Hora: 8:00 a.m. — 10:15 a.m.\n"
                        + "Lugar: sala de juntas principal.\n\n"
                        + "ASISTENTES\n"
                        + "Presidente, vicepresidente, gerente general, revisor fiscal e invitados.\n\n"
                        + "TEMAS\n"
                        + "1. Aprobación del presupuesto del cuarto trimestre.\n"
                        + "2. Avance del plan de transformación digital.\n"
                        + "3. Situación del proceso laboral en curso.\n"
                        + "4. Nombramiento del nuevo director de operaciones.\n\n"
                        + "DECISIONES\n"
                        + "Se aprueba por unanimidad el presupuesto presentado y se autoriza la "
                        + "contratación de la consultoría de seguridad de la información.\n\n"
                        + "La presente acta tiene reserva de dirección."
        ));
        documents.put("d7", new Document(
                "d7", "Política de privacidad", "Legal", "u2", "Andrés Rojas",
                Classification.INTERNAL, 760,
                "POLÍTICA DE TRATAMIENTO DE DATOS PERSONALES\n\n"
                        + "Bóveda S.A.S. actúa como responsable del tratamiento de los datos "
                        + "personales de sus clientes, proveedores y colaboradores.\n\n"
                        + "FINALIDADES\n"
                        + "• Gestión de la relación contractual.\n"
                        + "• Cumplimiento de obligaciones legales y tributarias.\n"
                        + "• Atención de solicitudes y canales de servicio.\n\n"
                        + "DERECHOS DEL TITULAR\n"
                        + "El titular puede conocer, actualizar, rectificar y solicitar la supresión "
                        + "de sus datos escribiendo a privacidad@boveda.co.\n\n"
                        + "VIGENCIA\n"
                        + "La política rige desde el 1 de enero de 2026 y se revisa anualmente."
        ));
        documents.put("d8", new Document(
                "d8", "Proveedores 2026", "Operaciones", "u4", "Carlos Ruiz",
                Classification.PUBLIC, 640,
                "DIRECTORIO DE PROVEEDORES 2026\n\n"
                        + "• Logística Andina S.A.S. — transporte y distribución nacional.\n"
                        + "• Papelería Central Ltda. — insumos de oficina y archivo.\n"
                        + "• Nube Segura S.A.S. — infraestructura y respaldo de información.\n"
                        + "• Mantenimiento Integral S.A.S. — servicios locativos.\n\n"
                        + "CANALES\n"
                        + "Compras: compras@boveda.co\n"
                        + "Pagos: tesoreria@boveda.co\n"
                        + "Radicación de facturas: facturacion@boveda.co\n\n"
                        + "Este directorio es de consulta pública para las áreas de la compañía."
        ));
        return documents;
    }
}
