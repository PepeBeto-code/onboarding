-- Eliminar el procedimiento si ya existe
DROP PROCEDURE IF EXISTS update_onboarding_step(BIGINT, INT);

-- Stored Procedure para actualizar el paso del onboarding
CREATE OR REPLACE PROCEDURE update_onboarding_step(user_id BIGINT, step INT)
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE onboarding
  SET step_completed = step,
      is_complete = CASE WHEN step >= 3 THEN true ELSE false END,
      updated_at = NOW()
  WHERE onboarding.user_id = update_onboarding_step.user_id;
END;
$$;

-- Eliminar tabla de estadísticas si ya existe
DROP TABLE IF EXISTS user_stats CASCADE;

-- Tabla para registrar cuántos usuarios se han insertado
CREATE TABLE user_stats (
    id SERIAL PRIMARY KEY,
    total_users INTEGER DEFAULT 0
);

-- Insertar fila inicial
INSERT INTO user_stats (total_users) VALUES (0);

-- Eliminar la función del trigger si ya existe
DROP FUNCTION IF EXISTS increment_user_count() CASCADE;

-- Trigger function para incrementar contador de usuarios
CREATE OR REPLACE FUNCTION increment_user_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE user_stats SET total_users = total_users + 1 WHERE id = 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Eliminar trigger si ya existe
DROP TRIGGER IF EXISTS user_insert_trigger ON users;

-- Trigger asociado a la tabla de usuarios
CREATE TRIGGER user_insert_trigger
AFTER INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION increment_user_count();
