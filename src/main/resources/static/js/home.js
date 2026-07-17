const API_BASE = '/alimento';
const API_RECEITA = '/receita';
let alimentoEdit = null;

// Carrega a lista de alimentos ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarAlimentos();
    document.getElementById('alimentoForm').addEventListener('submit', handleFormSubmit);
});

function carregarAlimentos() {
    document.getElementById('loadingList').style.display = 'block';
    document.getElementById('alimentosList').innerHTML = '';

    fetch(`${API_BASE}/listar/todos`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('loadingList').style.display = 'none';
            exibirAlimentos(data);
        })
        .catch(error => {
            document.getElementById('loadingList').style.display = 'none';
            mostrarAlerta('listAlert', 'Erro ao carregar alimentos: ' + error.message, 'danger');
        });
}

function exibirAlimentos(alimentos) {
    const container = document.getElementById('alimentosList');

    if (alimentos.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>Nenhum alimento adicionado ainda. Comece adicionando um!</p></div>';
        return;
    }

    let html = '<table><thead><tr><th>Nome</th><th>Categoria</th><th>Quantidade</th><th>Validade</th><th>Ações</th></tr></thead><tbody>';

    alimentos.forEach(alimento => {
        const dataValidade = new Date(alimento.validade).toLocaleDateString('pt-BR');
        html += `
            <tr>
                <td>${alimento.nome}</td>
                <td>${alimento.categoria}</td>
                <td>${alimento.quantidade}</td>
                <td>${dataValidade}</td>
                <td>
                    <div class="actions">
                        <button class="btn-edit" onclick="editarAlimento(${alimento.id})">Editar</button>
                        <button class="btn-danger" onclick="deletarAlimento(${alimento.id})">Deletar</button>
                    </div>
                </td>
            </tr>
        `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
}

function handleFormSubmit(e) {
    e.preventDefault();

    const nome = document.getElementById('nome').value;
    const categoria = document.getElementById('categoria').value;
    const quantidade = parseInt(document.getElementById('quantidade').value);
    const validade = document.getElementById('validade').value;

    const alimento = {
        nome,
        categoria,
        quantidade,
        validade
    };

    const url = alimentoEdit ? `${API_BASE}/alterar/${alimentoEdit}` : `${API_BASE}/salvar`;
    const metodo = alimentoEdit ? 'PUT' : 'POST';

    fetch(url, {
        method: metodo,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(alimento)
    })
        .then(response => response.json())
        .then(data => {
            const mensagem = alimentoEdit ? 'Alimento atualizado com sucesso!' : 'Alimento adicionado com sucesso!';
            mostrarAlerta('formAlert', mensagem, 'success');
            limparFormulario();
            carregarAlimentos();
        })
        .catch(error => {
            mostrarAlerta('formAlert', 'Erro ao salvar alimento: ' + error.message, 'danger');
        });
}

function editarAlimento(id) {
    fetch(`${API_BASE}/listar/${id}`)
        .then(response => response.json())
        .then(alimento => {
            alimentoEdit = id;
            document.getElementById('nome').value = alimento.nome;
            document.getElementById('categoria').value = alimento.categoria;
            document.getElementById('quantidade').value = alimento.quantidade;
            document.getElementById('validade').value = alimento.validade;
            
            window.scrollTo({ top: 0, behavior: 'smooth' });
            document.getElementById('nome').focus();
        })
        .catch(error => {
            mostrarAlerta('formAlert', 'Erro ao carregar alimento: ' + error.message, 'danger');
        });
}

function deletarAlimento(id) {
    if (!confirm('Tem certeza que deseja deletar este alimento?')) {
        return;
    }

    fetch(`${API_BASE}/deletar/${id}`, {
        method: 'DELETE'
    })
        .then(() => {
            mostrarAlerta('listAlert', 'Alimento deletado com sucesso!', 'success');
            carregarAlimentos();
        })
        .catch(error => {
            mostrarAlerta('listAlert', 'Erro ao deletar alimento: ' + error.message, 'danger');
        });
}

function limparFormulario() {
    document.getElementById('alimentoForm').reset();
    alimentoEdit = null;
    document.getElementById('formAlert').style.display = 'none';
}

function gerarReceita() {
    document.getElementById('receitaModal').style.display = 'block';
    document.getElementById('loadingReceita').style.display = 'block';
    document.getElementById('receitaContent').innerHTML = '';

    fetch(`${API_RECEITA}/gerar`, {
        method: 'POST'
    })
        .then(response => response.text())
        .then(receita => {
            document.getElementById('loadingReceita').style.display = 'none';
            document.getElementById('receitaContent').innerHTML = `<div class="receita-content">${escapeHtml(receita)}</div>`;
        })
        .catch(error => {
            document.getElementById('loadingReceita').style.display = 'none';
            document.getElementById('receitaContent').innerHTML = `<div class="alert alert-danger">Erro ao gerar receita: ${error.message}</div>`;
        });
}

function fecharModalReceita() {
    document.getElementById('receitaModal').style.display = 'none';
}

function mostrarAlerta(elementId, mensagem, tipo) {
    const alerta = document.getElementById(elementId);
    alerta.textContent = mensagem;
    alerta.className = `alert alert-${tipo}`;
    alerta.style.display = 'block';
    
    setTimeout(() => {
        alerta.style.display = 'none';
    }, 5000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Fechar modal ao clicar fora
window.onclick = function(event) {
    const modal = document.getElementById('receitaModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};
