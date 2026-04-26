const API_URL = "/api/v1/statistics";

export async function getIncomeStatistics(period: string, number: number): Promise<number[]> {
    const validatedPeriod = validate(period, number);

    const params = new URLSearchParams({ 
        period: validatedPeriod, 
        number: number.toString() 
    });

    const res = await fetch(`${API_URL}/income?${params.toString()}`, {
        credentials: "include"
    });

    if (!res.ok) {
        throw new Error(`Error al obtener los datos de los beneficios (${period}) para las estadísticas: ${res.status}`);
    }

    const json = await res.json();
    return json.data;
}

export async function getOrdersStatistics(period: string, number: number): Promise<number[]> {
    const validatedPeriod = validate(period, number);

    const params = new URLSearchParams({ 
        period: validatedPeriod, 
        number: number.toString() 
    });

    const res = await fetch(`${API_URL}/orders?${params.toString()}`, {
        credentials: "include"
    });

    if (!res.ok) {
        throw new Error(`Error al obtener los datos de los pedidos (${period}) para las estadísticas: ${res.status}`);
    }

    const json = await res.json();
    return json.data;
}

export async function getLabelsStatistics(period: string, number: number): Promise<number[]> {
    const validatedPeriod = validate(period, number);

    const params = new URLSearchParams({ 
        period: validatedPeriod, 
        number: number.toString() 
    });

    const res = await fetch(`${API_URL}/labels?${params.toString()}`, {
        credentials: "include"
    });

    if (!res.ok) {
        throw new Error(`Error al obtener los datos de las labels (${period}) para las estadísticas: ${res.status}`);
    }

    const json = await res.json();
    return json.data;
}

export async function getCategoryStatistics(): Promise<Record<string, number>> {
    const res = await fetch(`${API_URL}/categories`, {
        credentials: "include"
    });

    if (!res.ok) {
        throw new Error(`Error al obtener las estadísticas por categoría: ${res.status}`);
    }

    const json = await res.json();
    return json.data;
}

export async function getMeanTicket(
    userId: number,
    period: string,
    number: number
): Promise<number[]> {
    const validatedPeriod = validateForMeanTicket(period, number);

    const params = new URLSearchParams({ 
        period: validatedPeriod, 
        number: number.toString() 
    });

    const res = await fetch(`${API_URL}/users/${userId}?${params.toString()}`);

    if (!res.ok) {
        throw new Error(`Error al obtener el ticket medio (${period}) para las estadísticas`);
    }

    const json = await res.json();
    return json.data;
}

function validate(period: string, number: number): string {
    const validPeriods = ["day", "month", "year"];

    if (number <= 0 || number > 100) {
        throw new Error("El número de períodos debe ser mayor que cero y menor o igual a 100");
    }

    if (!validPeriods.includes(period)) {
        throw new Error("El periodo debe ser day, month o year");
    }

    return period;
}

function validateForMeanTicket(period: string, number: number) {
    const validPeriods = ["month", "year"];

    if (number <= 0) {
        throw new Error("El número debe ser mayor a 0");
    }

    if (!validPeriods.includes(period)) {
        throw new Error("Para el ticket medio, el periodo debe ser 'month' o 'year'");
    }

    return period;
}