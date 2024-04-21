INSERT INTO clientes
(nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone, data_criacao)
VALUES
('João Silva', '123.456.789-09', 'joao.silva@email.com', '12345-678', 'Rua das Flores', '100', 'Apto 101', 'Centro', 'São Paulo', 'SP', '(11) 91234-5678', CURRENT_TIMESTAMP),
('Maria Oliveira', '987.654.321-98', 'maria.oliveira@email.com', '98765-432', 'Avenida Brasil', '200', 'Casa 2', 'Jardim', 'Rio de Janeiro', 'RJ', '(21) 99876-5432', CURRENT_TIMESTAMP),
('Carlos Pereira', '456.123.789-01', 'carlos.pereira@email.com', '45678-912', 'Travessa Três', '300', 'Sala 5', 'Industrial', 'Belo Horizonte', 'MG', '(31) 92345-6789', CURRENT_TIMESTAMP),
('Ana Costa', '321.654.987-65', 'ana.costa@email.com', '87654-321', 'Alameda das Nações', '400', 'Bloco B', 'Comercial', 'Brasília', 'DF', '(61) 93456-7890', CURRENT_TIMESTAMP),
('Pedro Santos', '789.456.123-12', 'pedro.santos@email.com', '54321-654', 'Rua do Porto', '500', 'Loja 8', 'Porto', 'Porto Alegre', 'RS', '(51) 94567-8901', CURRENT_TIMESTAMP);