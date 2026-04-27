import { Collapse, Card, Form, Row, Col, Button } from "react-bootstrap";

interface GarmentFiltersProps {
    onFilter: (event: React.SubmitEvent) => void;
    show: boolean;
    currentParams: URLSearchParams;
}

export default function GarmentFilters({ onFilter, show, currentParams }: GarmentFiltersProps) {
    return (
        <Collapse in={show}>
            <div className="mb-5" id="filterCollapse">
                <Card className="border-0 shadow-sm bg-light">
                    <Card.Body>
                        <Form onSubmit={onFilter}>
                            <Row className="g-3">

                                <Col xs={12} md={3}>
                                    <Form.Label
                                        className="small fw-bold text-uppercase text-muted"
                                        style={{ fontSize: "0.7rem" }}
                                    >
                                        Buscar
                                    </Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="nameSearch"
                                        placeholder="Ej: Pantalón denim..."
                                        defaultValue={currentParams.get("nameSearch") || ""}
                                        size="sm"
                                        className="border-0 shadow-sm"
                                    />
                                </Col>

                                <Col xs={6} md={2}>
                                    <Form.Label
                                        className="small fw-bold text-uppercase text-muted"
                                        style={{ fontSize: "0.7rem" }}
                                    >
                                        Categoría
                                    </Form.Label>
                                    <Form.Select
                                        name="categorySearch"
                                        defaultValue={currentParams.get("categorySearch") || ""}
                                        size="sm"
                                        className="border-0 shadow-sm"
                                    >
                                        <option value="">Todas</option>
                                        <option value="Camisas">Camisas</option>
                                        <option value="Pantalones">Pantalones</option>
                                        <option value="Zapatos">Zapatos</option>
                                        <option value="Chaquetas">Chaquetas</option>
                                        <option value="Accesorios">Accesorios</option>
                                    </Form.Select>
                                </Col>

                                <Col xs={6} md={3}>
                                    <Form.Label
                                        className="small fw-bold text-uppercase text-muted"
                                        style={{ fontSize: "0.7rem" }}
                                    >
                                        Rango de Precio (€)
                                    </Form.Label>

                                    <div className="d-flex align-items-center gap-2">
                                        <Form.Control
                                            type="number"
                                            name="minPrice"
                                            placeholder="Mín"
                                            defaultValue={currentParams.get("minPrice") || ""}
                                            size="sm"
                                            className="border-0 shadow-sm"
                                        />
                                        <span className="text-muted small">-</span>
                                        <Form.Control
                                            type="number"
                                            name="maxPrice"
                                            placeholder="Máx"
                                            defaultValue={currentParams.get("maxPrice") || ""}
                                            size="sm"
                                            className="border-0 shadow-sm"
                                        />
                                    </div>
                                </Col>

                                <Col xs={6} md={2}>
                                    <Form.Label
                                        className="small fw-bold text-uppercase text-muted"
                                        style={{ fontSize: "0.7rem" }}
                                    >
                                        Ordenar
                                    </Form.Label>
                                    <Form.Select
                                        name="sort"
                                        defaultValue={currentParams.get("sort") || ""}
                                        size="sm"
                                        className="border-0 shadow-sm"
                                    >
                                        <option value="">Seleccione</option>
                                        <option value="price,asc">Precio: Menor a Mayor</option>
                                        <option value="price,desc">Precio: Mayor a Menor</option>
                                    </Form.Select>
                                </Col>

                                <Col xs={6} md={2} className="d-flex align-items-end">
                                    <Button
                                        type="submit"
                                        size="sm"
                                        className="w-100 fw-bold text-uppercase shadow-sm"
                                        variant="dark"
                                    >
                                        Aplicar
                                    </Button>
                                </Col>

                            </Row>
                        </Form>
                    </Card.Body>
                </Card>
            </div>
        </Collapse>
    );
}