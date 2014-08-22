/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.client.auth.credentials;

import io.divide.shared.transitory.Credentials;

import java.security.PrivateKey;
import java.security.PublicKey;

/*
 * Credentials used for logging in
 */
public class LoginCredentials extends Credentials {

    private boolean isEncrypted = false;

    public LoginCredentials(String email, String password){
        this.setEmailAddress(email);
        this.setPassword(password);
    }

    @Override
    public void decryptPassword(PrivateKey privateKey){
        if(isEncrypted()){
            super.decryptPassword(privateKey);
            setEncrypted(false);
        }
    }

    @Override
    public void encryptPassword(PublicKey publicKey){
        if(!isEncrypted()){
            super.encryptPassword(publicKey);
            setEncrypted(true);
        }
    }

    public boolean isEncrypted(){
        return isEncrypted;
    }

    public void setEncrypted(boolean isEncrypted){
        this.isEncrypted = isEncrypted;
    }

}
