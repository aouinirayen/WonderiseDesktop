-- Add auteur column to commentaire table if it doesn't exist
ALTER TABLE commentaire
ADD COLUMN IF NOT EXISTS auteur VARCHAR(255) DEFAULT 'Anonymous';

-- Update existing rows to have a default value
UPDATE commentaire SET auteur = 'Anonymous' WHERE auteur IS NULL; 