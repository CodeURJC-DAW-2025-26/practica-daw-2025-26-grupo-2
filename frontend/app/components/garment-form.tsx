import "./garment-form.css";
import { useState } from "react";
import type GarmentExtendedDTO from "~/dtos/GarmentExtendedDTO";
import {
    Container,
    Form,
    InputGroup,
    Button,
    Alert,
    Spinner,
} from "react-bootstrap";

interface GarmentFormProps {
    garment?: GarmentExtendedDTO;
    formAction: (payload: FormData) => void;
    isPending: boolean;
    error: string | null;
}

export default function GarmentForm({ garment, formAction, isPending, error }: GarmentFormProps) {
    const isEdit = !!garment;
    const [updateImage, setUpdateImage] = useState(false);
    const [validated, setValidated] = useState(false); // For validation

    const handleSubmit = (event: React.SubmitEvent<HTMLFormElement>) => {
        const form = event.currentTarget;

        // If form si not valid
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }

        setValidated(true);
    };

    return (
        <Container>
            <div className="container-header-form">
                <h3>{isEdit ? "Editar prenda" : "Añadir nueva prenda"}</h3>
            </div>

            {error && <Alert variant="danger" className="mt-3">{error}</Alert>}

            <hr className="gap-form" />

            <Form
                action={formAction}
                noValidate
                validated={validated}
                onSubmit={handleSubmit} // For validation
            >
                {garment?.id && <input type="hidden" name="id" value={garment.id} />}

                {/* NAME */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Nombre</b></Form.Label>
                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-alphabet"></i>
                        </InputGroup.Text>
                        <Form.Control
                        type="text"
                        name="name"
                        placeholder="Nombre de la prenda"
                        defaultValue={garment?.name || ""}
                        minLength={4}
                        maxLength={100}
                        required
                        />
                        <Form.Control.Feedback type="invalid">
                            El nombre no puede estar vacío y debe tener entre 4 y 100 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* CATEGORY */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Categoría</b></Form.Label>
                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-tag"></i>
                        </InputGroup.Text>
                        <Form.Select
                        name="category"
                        defaultValue={garment?.category || ""}
                        required
                        >
                            {!garment && <option value="">Selecciona categoría</option>}
                            <option value="Camisas">Camisas</option>
                            <option value="Pantalones">Pantalones</option>
                            <option value="Zapatos">Zapatos</option>
                            <option value="Chaquetas">Chaquetas</option>
                            <option value="Accesorios">Accesorios</option>
                            <option value="Otros">Otros</option>
                        </Form.Select>
                        <Form.Control.Feedback type="invalid">
                            La categoría no puede estar vacía.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* PRICE */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Precio</b></Form.Label>
                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-cash-coin"></i>
                        </InputGroup.Text>
                        <Form.Control
                        type="number"
                        name="price"
                        step="0.01"
                        placeholder="Precio de la prenda"
                        defaultValue={garment?.price}
                        min={1}
                        max={6000}
                        required
                        />
                        <InputGroup.Text>€</InputGroup.Text>
                        <Form.Control.Feedback type="invalid">
                            El precio debe ser un número entre 1 y 6000.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* IMAGE */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Imagen</b></Form.Label>

                    {isEdit && garment?.image && (
                        <>
                        <div className="mb-3">
                            <img
                            src={`/api/v1/images/${garment.image.id}/media`}
                            alt={garment.name}
                            className="img-fluid mb-2"
                            style={{ maxWidth: "200px" }}
                            />
                        </div>

                        <Form.Check
                            className="mb-2"
                            type="checkbox"
                            id="updateImage"
                            name="updateImage"
                            label="Actualizar imagen"
                            checked={updateImage}
                            onChange={(e) => setUpdateImage(e.target.checked)}
                        />
                        </>
                    )}

                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-image"></i>
                        </InputGroup.Text>
                        <Form.Control
                        type="file"
                        name="imageFile"
                        accept=".jpg,.jpeg,.png"
                        required={!isEdit || updateImage}
                        disabled={isEdit && !updateImage}
                        />
                        <Form.Control.Feedback type="invalid">
                            {isEdit
                                ? 'Si no quieres actualizar la imagen, desmarca la casilla "Actualizar imagen".'
                                : "La imagen no puede estar vacía."}
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* DESCRIPTION */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Descripción</b></Form.Label>
                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-list-ul"></i>
                        </InputGroup.Text>
                        <Form.Control
                        as="textarea"
                        name="description"
                        rows={5}
                        placeholder="Descripción de la prenda"
                        defaultValue={garment?.description || ""}
                        minLength={3}
                        maxLength={200}
                        required
                        />
                        <Form.Control.Feedback type="invalid">
                            La descripción debe tener entre 3 y 200 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* FEATURES */}
                <Form.Group className="mb-3">
                    <Form.Label><b>Características</b></Form.Label>
                    <InputGroup>
                        <InputGroup.Text>
                            <i className="bi bi-list-ul"></i>
                        </InputGroup.Text>
                        <Form.Control
                        as="textarea"
                        name="features"
                        rows={5}
                        placeholder="Características de la prenda"
                        defaultValue={garment?.features || ""}
                        minLength={5}
                        maxLength={300}
                        required
                        />
                        <Form.Control.Feedback type="invalid">
                            Las características deben tener entre 5 y 300 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* SUBMIT */}
                <div className="mt-4">
                    <Button
                        type="submit"
                        variant="success"
                        className="text-dark"
                        disabled={isPending}
                    >
                        {isPending ? (
                        <>
                            <Spinner size="sm" className="me-2" />
                            Guardando...
                        </>
                        ) : (
                        <>
                            Guardar <i className="bi bi-check-lg"></i>
                        </>
                        )}
                    </Button>
                </div>
            </Form>
        </Container>
    );
}