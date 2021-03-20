import axios from "axios";
import React, {Component} from "react";
import SearchbarComponent from "../searchbar/SearchbarComponent";
import {Modal, Button, Form} from 'react-bootstrap';

export default class ReviewWriteBox extends Component {

    constructor(props) {
        super(props);
        this.state = {
            show: false,
            reviewData: {
                movieTitle: null,
                comment: null,
                rating: null,
                imgFiles: null,
            },
            title : ''
        }

        //Method binding
        this.handleClose = this.handleClose.bind(this);
        this.handleShow = this.handleShow.bind(this);
        this.handleSummit = this.handleSummit.bind(this);
        this.setMovieTitle = this.setMovieTitle.bind(this);

        //Input Value
        this.movieTitle = React.createRef();
        this.rating = React.createRef();
        this.comment = React.createRef();
        this.fileInput = React.createRef();
    }

    // 자식 컴포넌트로부터 영화명 수신 (확인 완료)
    setMovieTitle = (selectedMovieTitle) =>{
        this.setState({
            title: selectedMovieTitle,
        })
    }

    handleClose(e) {
        this.setState({show: false});
    }

    handleShow(e) {
        this.setState({show: true});
    }

    handleSummit(e) {
        const formData = new FormData();
        formData.append("movieTitle", this.state.title);
        console.log('form(title)' + this.state.title);
        formData.append("content", this.comment.current.value);

        let files = [];
        for (let i = 0; i < this.fileInput.current.files.length; i++) {
            files.push(this.fileInput.current.files[i]);
        }
        formData.append("file", this.fileInput.current.files[0]);
        formData.append("rating", this.rating.current.value);


        const token = localStorage.getItem("token");

        for (let i = 0; i < localStorage.length; i++) {
            console.log(localStorage.key(i) + " : " + localStorage.getItem(localStorage.key(i)));
        }

        let config = {
            headers: {
                'Content-Type': "multipart/form-data",
                'Authorization': 'Bearer ' + token,
            }
        }

        axios.post(`http://localhost:8080/api/review`, formData, config)
            .then(response => {
                alert("리뷰등록 성공(Demo)");
                this.handleClose();
                // console.log('response', JSON.stringify(response, null, 2))
            })
            .catch(error => {
                console.log('failed', error)
            })

    }

    render() {
        return (
            <>
                <button className="btn btn-primary" onClick={this.handleShow}>
                    +
                </button>
                <Modal show={this.state.show} onHide={this.handleClose}>
                    <Modal.Header closeButton style={this.state.style}>
                        <Form.Group controlId="reviewTitle">
                            {/*영화 서치바*/}
                            <SearchbarComponent callbackFromParent={this.setMovieTitle}/>
                            {/*<Form.Control type="text" placeholder="영화" ref={this.movieTitle} />*/}
                        </Form.Group>
                    </Modal.Header>
                    <Modal.Body style={this.state.style}>
                        <Form>
                            <Form.Group controlId="reviewRating">
                                <Form.Label>평점</Form.Label>
                                <Form.Control ref={this.rating} as="select">
                                    <option>1</option>
                                    <option>2</option>
                                    <option>3</option>
                                    <option>4</option>
                                    <option>5</option>
                                    <option>6</option>
                                    <option>7</option>
                                    <option>8</option>
                                    <option>9</option>
                                    <option>10</option>
                                </Form.Control>
                            </Form.Group>
                            <Form.Group controlId="reviewContent">
                                <Form.Label>Contents</Form.Label>
                                <Form.Control as="textarea" rows={3} ref={this.comment}/>
                            </Form.Group>
                            <Form.Group controlId="reviewImgFile">
                                <input type="file" multiple className="reviewImage" ref={this.fileInput}/>
                            </Form.Group>

                        </Form>
                    </Modal.Body>
                    <Modal.Footer style={this.state.style}>
                        <Button variant="secondary" onClick={this.handleClose}>
                            취소
                        </Button>
                        <Button variant="primary" onClick={this.handleSummit}>
                            등록
                        </Button>
                    </Modal.Footer>
                </Modal>

            </>
        )
    }

}
