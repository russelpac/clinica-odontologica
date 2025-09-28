PRAGMA foreign_keys = ON;
CREATE TABLE IF NOT EXISTS paciente (
    ID TEXT PRIMARY KEY,
    nombres TEXT NOT NULL,
    CI TEXT UNIQUE NOT NULL,
    apellidos TEXT,
    numeroContacto TEXT,
    fechaNacimiento TEXT,
    alergias TEXT,
    consultas TEXT,
    sexo TEXT,
    contactoEmergencias TEXT,
    direccion TEXT,
    antecMedicos TEXT,
    activo INTEGER DEFAULT 1,
    fechaConsulta TEXT
);

CREATE TABLE IF NOT EXISTS odontologo(
    ID TEXT PRIMARY KEY,
    nombre TEXT,
    numeroCelular TEXT,
    especialidad TEXT
);

CREATE TABLE IF NOT EXISTS pago(
    ID TEXT PRIMARY KEY,
    pacienteID TEXT NOT NULL,
    odontologoID TEXT,
    fecha TEXT NOT NULL,
    monto NUMERIC NOT NULL,
    metodo TEXT NOT NULL,
    estado TEXT NOT NULL,
    FOREIGN KEY (pacienteID) REFERENCES paciente(ID) ON DELETE RESTRICT,
    FOREIGN KEY (odontologoID) REFERENCES odontologo(ID) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_paciente_ci ON paciente(CI);
CREATE INDEX IF NOT EXISTS idx_pago_paciente ON pago(pacienteID);