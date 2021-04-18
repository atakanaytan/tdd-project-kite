import React from 'react';
import { render } from '@testing-library/react';
import KiteSubmit from './KiteSubmit';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import authReducer from '../redux/authReducer';

const defaultState = {
    id: 1,
    username: 'user1',
    displayName: 'display1',
    image: 'profile1.png',
    password: 'P4ssword',
    isLoggedIn: true
}

let store;

const setup = (state = defaultState) => {
    store = createStore(authReducer, state);
    return render(
        <Provider store={store}>
            <KiteSubmit />
        </Provider>
    );
};

describe('KiteSubmit', () => {
    describe('Layout', () => {
        it('has textarea', () => {
            const { container } = setup();
            const textarea = container.querySelector('textarea');
            expect(textarea).toBeInTheDocument();
        });
        it('has image', () => {
            const { container } = setup();
            const image = container.querySelector('img');
            expect(image).toBeInTheDocument();
        });
        it('displays textarea 1 line', () => {
            const { container } = setup();
            const textArea = container.querySelector('textarea');
            expect(textArea.rows).toBe(1);
        });
        it('displays user image', () => {
            const { container } = setup();
            const image = container.querySelector('img');
            expect(image.src).toContain('/images/profile/' + defaultState.image); 
        });
    }); 
});