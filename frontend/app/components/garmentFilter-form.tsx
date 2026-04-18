
interface GarmentFiltersProps {
    onFilter: (event: React.SubmitEvent) => void;
    show: boolean;
    currentParams: URLSearchParams;
}

export default function GarmentFilters({ onFilter, show, currentParams }: GarmentFiltersProps) {
    return (
        <div className={`collapse mb-5 ${show ? "show" : ""}`} id="filterCollapse">
        <div className="card card-body border-0 shadow-sm bg-light">
            <form onSubmit={onFilter}>
            <div className="row g-3">
                <div className="col-12 col-md-3">
                <label className="form-label small fw-bold text-uppercase text-muted" style={{ fontSize: "0.7rem" }}>Buscar</label>
                <input 
                    type="text" 
                    name="nameSearch"
                    className="form-control form-control-sm border-0 shadow-sm" 
                    placeholder="Ej: Pantalón denim..." 
                    defaultValue={currentParams.get("nameSearch") || ""}
                />
                </div>

                <div className="col-6 col-md-2">
                <label className="form-label small fw-bold text-uppercase text-muted" style={{ fontSize: "0.7rem" }}>Categoría</label>
                <select 
                    name="categorySearch"
                    className="form-select form-select-sm border-0 shadow-sm"
                    defaultValue={currentParams.get("categorySearch") || ""}
                >
                    <option value="">Todas</option>
                    <option value="Camisas">Camisas</option>
                    <option value="Pantalones">Pantalones</option>
                    <option value="Zapatos">Zapatos</option>
                    <option value="Chaquetas">Chaquetas</option>
                    <option value="Accesorios">Accesorios</option>
                </select>
                </div>

                <div className="col-6 col-md-3">
                <label className="form-label small fw-bold text-uppercase text-muted" style={{ fontSize: "0.7rem" }}>Rango de Precio (€)</label>
                <div className="d-flex align-items-center gap-2">
                    <input 
                    type="number" name="minPrice" className="form-control form-control-sm border-0 shadow-sm" placeholder="Mín" 
                    defaultValue={currentParams.get("minPrice") || ""}
                    />
                    <span className="text-muted small">-</span>
                    <input 
                    type="number" name="maxPrice" className="form-control form-control-sm border-0 shadow-sm" placeholder="Máx" 
                    defaultValue={currentParams.get("maxPrice") || ""}
                    />
                </div>
                </div>

                <div className="col-6 col-md-2">
                <label className="form-label small fw-bold text-uppercase text-muted" style={{ fontSize: "0.7rem" }}>Ordenar</label>
                <select 
                    name="sort"
                    className="form-select form-select-sm border-0 shadow-sm"
                    defaultValue={currentParams.get("sort") || ""}
                >
                    <option value="">Seleccione</option>
                    <option value="price,asc">Precio: Menor a Mayor</option>
                    <option value="price,desc">Precio: Mayor a Menor</option>
                </select>
                </div>

                <div className="col-6 col-md-2 d-flex align-items-end">
                <button className="btn btn-dark btn-sm w-100 fw-bold text-uppercase shadow-sm" type="submit">
                    Aplicar
                </button>
                </div>
            </div>
            </form>
        </div>
        </div>
    );
}