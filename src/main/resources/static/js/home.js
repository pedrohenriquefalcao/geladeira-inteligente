const API_BASE = '/alimento';
const API_RECEITA = '/receita';
let alimentoEdit = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarAlimentos();
    document.getElementById('alimentoForm').addEventListener('submit', handleFormSubmit);
    configurarTema();
    configurarValidadeMinima();
});

function configurarTema() {
    const temaSalvo = localStorage.getItem('geladeira-theme');
    const temaInicial = temaSalvo === 'dark' ? 'dark' : 'light';
    aplicarTema(temaInicial);
    document.getElementById('themeToggle').addEventListener('click', () => {
        aplicarTema(document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark');
    });
}

function aplicarTema(tema) {
    const toggle = document.getElementById('themeToggle');
    document.documentElement.dataset.theme = tema;
    toggle.setAttribute('aria-pressed', tema === 'dark');
    toggle.setAttribute('aria-label', tema === 'dark' ? 'Ativar modo claro' : 'Ativar modo escuro');
    toggle.querySelector('span').textContent = tema === 'dark' ? '☀' : '☾';
    toggle.querySelector('.theme-label').textContent = tema === 'dark' ? 'Modo claro' : 'Modo escuro';
    localStorage.setItem('geladeira-theme', tema);
}

function configurarValidadeMinima() {
    const dataMinima = new Date();
    dataMinima.setDate(dataMinima.getDate() + 1);
    document.getElementById('validade').min = dataMinima.toISOString().split('T')[0];
}

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

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    alimentos.forEach(alimento => {
        const [ano, mes, dia] = alimento.validade.split('-');
        const dataValidadeObj = new Date(ano, mes - 1, dia);

        const diffTempo = dataValidadeObj.getTime() - hoje.getTime();
        const diffDias = Math.ceil(diffTempo / (1000 * 3600 * 24));

        const dataFormatada = dataValidadeObj.toLocaleDateString('pt-BR');

        let validadeHtml = dataFormatada;

        if (diffDias < 0) {
            validadeHtml = `<span class="badge-danger-date" title="Produto Vencido!">${dataFormatada}</span>`;
        } else if (diffDias <= 7) {
            validadeHtml = `<span class="badge-warning-date" title="Vence em ${diffDias} dia(s)">${dataFormatada}</span>`;
        }

        html += `
            <tr>
                <td style="font-weight: 500;">${alimento.nome}</td>
                <td><span style="background: var(--bg-color); padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; border: 1px solid var(--border);">${alimento.categoria}</span></td>
                <td>${alimento.quantidade}</td>
                <td>${validadeHtml}</td>
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

    const nome = document.getElementById('nome').value.trim();
    const categoria = document.getElementById('categoria').value;
    const quantidade = parseInt(document.getElementById('quantidade').value, 10);
    const validade = document.getElementById('validade').value;

    const [ano, mes, dia] = validade.split('-');
    const dataSelecionada = new Date(ano, mes - 1, dia);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    if (!nome || nome.length < 2) {
        mostrarAlerta('formAlert', 'Informe um nome de alimento válido.', 'warning');
        document.getElementById('nome').focus();
        return;
    }

    if (!categoria) {
        mostrarAlerta('formAlert', 'Selecione uma categoria para o alimento.', 'warning');
        document.getElementById('categoria').focus();
        return;
    }

    if (!Number.isInteger(quantidade) || quantidade < 1 || quantidade > 999) {
        mostrarAlerta('formAlert', 'A quantidade deve estar entre 1 e 999.', 'warning');
        document.getElementById('quantidade').focus();
        return;
    }

    if (!validade || dataSelecionada <= hoje) {
        mostrarAlerta('formAlert', 'A data de validade deve ser MAIOR que o dia atual.', 'warning');
        document.getElementById('validade').focus();
        return;
    }

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

let idParaDeletar = null;

function deletarAlimento(id) {
    idParaDeletar = id;
    document.getElementById('confirmModal').style.display = 'flex';
}

function fecharModalConfirmacao() {
    document.getElementById('confirmModal').style.display = 'none';
    idParaDeletar = null;
}

document.getElementById('btnConfirmarExclusao').addEventListener('click', () => {
    if (!idParaDeletar) return;

    fetch(`${API_BASE}/deletar/${idParaDeletar}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao comunicar com o servidor');

            fecharModalConfirmacao();
            mostrarAlerta('listAlert', 'Alimento removido.', 'success');
            carregarAlimentos();
        })
        .catch(error => {
            fecharModalConfirmacao();
            mostrarAlerta('listAlert', 'Erro ao excluir: ' + error.message, 'danger');
        });
});

window.onclick = function(event) {
    const confirmModal = document.getElementById('confirmModal');
    if (event.target === confirmModal) {
        fecharModalConfirmacao();
    }
};

function limparFormulario() {
    document.getElementById('alimentoForm').reset();
    alimentoEdit = null;
    document.getElementById('formAlert').style.display = 'none';
}

function abrirSidebarReceita() {
    document.getElementById('recipeCard').scrollIntoView({ behavior: 'smooth', block: 'center' });
    gerarReceita();
}

function fecharSidebarReceita() {
    document.getElementById('recipeCard').focus();
}

function gerarReceita() {
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