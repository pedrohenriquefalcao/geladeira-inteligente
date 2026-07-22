const API_BASE = '/alimento';
const API_RECEITA = '/receita';
let alimentoEdit = null;

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
        container.innerHTML = `
            <div class="empty-state">
                <p>Nenhum alimento na geladeira.</p>
                <p style="font-size: 0.85rem; margin-top: 0.5rem;">Adicione itens pelo formulário ao lado.</p>
            </div>`;
        return;
    }

    let html = `
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Categoria</th>
                    <th>Qtd</th>
                    <th>Validade</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
    `;

    alimentos.forEach(alimento => {
        const dataValidade = new Date(alimento.validade).toLocaleDateString('pt-BR');
        html += `
            <tr>
                <td style="font-weight: 500;">${alimento.nome}</td>
                <td><span style="background: var(--bg-color); padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; border: 1px solid var(--border);">${alimento.categoria}</span></td>
                <td>${alimento.quantidade}</td>
                <td>${dataValidade}</td>
                <td>
                    <div class="actions">
                        <button class="btn-edit" onclick="editarAlimento(${alimento.id})">Editar</button>
                        <button class="btn-danger" onclick="deletarAlimento(${alimento.id})">Excluir</button>
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

    const alimento = { nome, categoria, quantidade, validade };

    const url = alimentoEdit ? `${API_BASE}/alterar/${alimentoEdit}` : `${API_BASE}/salvar`;
    const metodo = alimentoEdit ? 'PUT' : 'POST';

    fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(alimento)
    })
        .then(response => {
            if (!response.ok) throw new Error('Erro na resposta do servidor');
            return response.json();
        })
        .then(() => {
            const mensagem = alimentoEdit ? 'Alimento atualizado!' : 'Alimento adicionado!';
            mostrarAlerta('formAlert', mensagem, 'success');
            limparFormulario();
            carregarAlimentos();
        })
        .catch(error => {
            mostrarAlerta('formAlert', 'Erro ao salvar: ' + error.message, 'danger');
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
            mostrarAlerta('listAlert', 'Erro ao carregar dados: ' + error.message, 'danger');
        });
}

function deletarAlimento(id) {
    if (!confirm('Excluir este alimento da geladeira?')) return;

    fetch(`${API_BASE}/deletar/${id}`, { method: 'DELETE' })
        .then(() => {
            mostrarAlerta('listAlert', 'Alimento removido.', 'success');
            carregarAlimentos();
        })
        .catch(error => {
            mostrarAlerta('listAlert', 'Erro ao excluir: ' + error.message, 'danger');
        });
}

function limparFormulario() {
    document.getElementById('alimentoForm').reset();
    alimentoEdit = null;
    document.getElementById('formAlert').style.display = 'none';
}

function gerarReceita() {
    document.getElementById('receitaModal').style.display = 'flex';
    document.getElementById('loadingReceita').style.display = 'block';
    document.getElementById('receitaContent').innerHTML = '';

    fetch(`${API_RECEITA}/gerar`, { method: 'POST' })
        .then(response => {
            if (!response.ok) throw new Error('Falha ao comunicar com a IA');
            return response.text();
        })
        .then(receita => {
            document.getElementById('loadingReceita').style.display = 'none';
            document.getElementById('receitaContent').innerHTML = `<div class="receita-content">${escapeHtml(receita)}</div>`;
        })
        .catch(error => {
            document.getElementById('loadingReceita').style.display = 'none';
            document.getElementById('receitaContent').innerHTML = `<div class="alert alert-danger" style="display:block; margin-top: 1rem;">Erro: ${error.message}</div>`;
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
    }, 4000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

window.onclick = function(event) {
    const modal = document.getElementById('receitaModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};